"""
    Based on: https://gist.github.com/xjcl/8ce64008710128f3a076
    Modified by PedroLopes for openEMSstim but credit remains with author

    HOW TO SETUP:
    1. Connect the openEMSstim via USB to your laptop
    2. Setup the serial_port variable (e.g., serial_port = "/dev/tty.wchusbserial1410")
    3. Connect Player 1 to EMS Channel 1 and Player 2 to EMS Channel 2
    4. Calibrate openEMSstim (read plopes.org/ems to learn how to safely execute this)
    5. Start the python game: >python ems-pong.py

    HOW TO PLAY:
    Player 0 controls: UP (W) DOWN (S)  
    Player 1 controls: UP (O) DOWN (L). 
    Quit: (Q)
    
    HOW TO INSTALL:
    Requires: pyglet, openEMSstim python lib (openems) 
    and a 10x10 image in directory: "white_square.png"
"""
#native imports
import math
import random
import pyglet
import sys

#import openEMSstim modules
sys.path.append("../")
from pyEMS import openEMSstim
from pyEMS.EMSCommand import ems_command

serial_port = "/dev/tty.wchusbserial1410"

class Ball(object):

    def __init__(self):
        self.debug = 0
        self.TO_SIDE = 5
        self.x = 50.0 + self.TO_SIDE
        self.y = float( random.randint(0, 450) )
        self.x_old = self.x  # coordinates in the last frame
        self.y_old = self.y
        self.vec_x = 2**0.5 / 2  # sqrt(2)/2
        self.vec_y = random.choice([-1, 1]) * 2**0.5 / 2

class Player(object):

    def __init__(self, NUMBER, screen_WIDTH=800):
        """NUMBER must be 0 (left player) or 1 (right player)."""
        self.NUMBER = NUMBER
        self.x = 50.0 + (screen_WIDTH - 100) * NUMBER
        self.y = 50.0
        self.last_movements = [0]*4  # short movement history
                                     # used for bounce calculation
        self.up_key, self.down_key = None, None
        if NUMBER == 0:
            self.up_key = pyglet.window.key.W
            self.down_key = pyglet.window.key.S
        elif NUMBER == 1:
            self.up_key = pyglet.window.key.O
            self.down_key = pyglet.window.key.L


class Model(object):
    """Model of the entire game. Has two players and one ball."""

    def __init__(self, DIMENSIONS=(800, 450)):
        """DIMENSIONS is a tuple (WIDTH, HEIGHT) of the field."""
        # OBJECTS
        WIDTH = DIMENSIONS[0]
        self.players = [Player(0, WIDTH), Player(1, WIDTH)]
        self.ball = Ball()
        # DATA
        self.pressed_keys = set()  # set has no duplicates
        self.quit_key = pyglet.window.key.Q
        self.speed = 6  # in pixels per frame
        self.ball_speed = self.speed * 2.5
        self.WIDTH, self.HEIGHT = DIMENSIONS
        # STATE VARS
        self.paused = False
        self.i = 0  # "frame count" for debug
        self.ems_device = openEMSstim.openEMSstim(serial_port,19200)
        self.left_player_lost_stimulation = ems_command(1,100,2000)
        self.right_player_lost_stimulation = ems_command(2,100,2000)

    def reset_ball(self, who_scored):
        """Place the ball anew on the loser's side."""
        if debug: print(str(who_scored)+" scored. reset.")
        self.ball.y = float( random.randint(0, self.HEIGHT) )
        self.ball.vec_y = random.choice([-1, 1]) * 2**0.5 / 2
        if who_scored == 0:
            self.ball.x = self.WIDTH - 50.0 - self.ball.TO_SIDE
            self.ball.vec_x = - 2**0.5 / 2
        elif who_scored == 1:
            self.ball.x = 50.0 + self.ball.TO_SIDE
            self.ball.vec_x = + 2**0.5 / 2
        elif who_scored == "debug":
            self.ball.x = 70  # in paddle atm -> usage: hold f
            self.ball.y = self.ball.debug
            self.ball.vec_x = -1
            self.ball.vec_y = 0
            self.ball.debug += 0.2
            if self.ball.debug > 100:
                self.ball.debug = 0

    def check_if_oob_top_bottom(self):
        """Called by update_ball to recalc. a ball above/below the screen."""
        # bounces. if -- bounce on top of screen. elif -- bounce on bottom.
        b = self.ball
        if b.y - b.TO_SIDE < 0:
            illegal_movement = 0 - (b.y - b.TO_SIDE)
            b.y = 0 + b.TO_SIDE + illegal_movement
            b.vec_y *= -1
        elif b.y + b.TO_SIDE > self.HEIGHT:
            illegal_movement = self.HEIGHT - (b.y + b.TO_SIDE)
            b.y = self.HEIGHT - b.TO_SIDE + illegal_movement
            b.vec_y *= -1

    def check_if_oob_sides(self):
        """Called by update_ball to reset a ball left/right of the screen."""
        b = self.ball
        if b.x + b.TO_SIDE < 0:  # leave on left
            self.reset_ball(1)
            self.ems_device.send(self.left_player_lost_stimulation)
        elif b.x - b.TO_SIDE > self.WIDTH:  # leave on right
            self.reset_ball(0)
            self.ems_device.send(self.right_player_lost_stimulation)

    def check_if_paddled(self):
        """Called by update_ball to recalc. a ball hit with a player paddle."""
        # TODO remove duplicate code?
        # TODO also retrace the original line to see if it touches paddle
        b = self.ball
        p0, p1 = self.players[0], self.players[1]
        angle = math.acos(b.vec_y)  # vector mult: (0, 1) * (b.vec_x, b.vec_y)
        factor = random.randint(5, 15)  # low: strong player influence
        # `cross` is true for player n if the ball was in front of their
        # paddle the frame before and is behind them in this frame
        cross0 = (b.x < p0.x + 2*b.TO_SIDE) and (b.x_old >= p0.x + 2*b.TO_SIDE)
        cross1 = (b.x > p1.x - 2*b.TO_SIDE) and (b.x_old <= p1.x - 2*b.TO_SIDE)
        if cross0 and -25 < b.y - p0.y < 25:
            if debug: print("hit at "+str(self.i))
            illegal_movement = p0.x + 2*b.TO_SIDE - b.x
            b.x = p0.x + 2*b.TO_SIDE + illegal_movement
            angle -= sum(p0.last_movements) / factor / self.ball_speed
            b.vec_y = math.cos(angle)
            b.vec_x = (1**2 - b.vec_y**2) ** 0.5
        elif cross1 and -25 < b.y - p1.y < 25:
            if debug: print("hit at "+str(self.i))
            illegal_movement = p1.x - 2*b.TO_SIDE - b.x
            b.x = p1.x - 2*b.TO_SIDE + illegal_movement
            angle -= sum(p1.last_movements) / factor / self.ball_speed
            b.vec_y = math.cos(angle)
            b.vec_x = - (1**2 - b.vec_y**2) ** 0.5

    def update_ball(self):
        """
            Update ball position with post-collision detection.
            I.e. Let the ball move out of bounds and calculate
            where it should have been within bounds.

            When bouncing off a paddle, take player velocity into
            consideration as well. Add a small factor of random too.
        """
        self.i += 1  # "debug"
        b = self.ball
        b.x_old, b.y_old = b.x, b.y
        b.x += b.vec_x * self.ball_speed
        b.y += b.vec_y * self.ball_speed
        self.check_if_oob_top_bottom()  # oob: out of bounds
        self.check_if_oob_sides()
        self.check_if_paddled()

    def update(self):
        """Work through all pressed keys, update and call update_ball."""
        pks = self.pressed_keys
        if self.quit_key in pks:
            exit(0)
        if pyglet.window.key.R in pks and debug:
            self.reset_ball(1)
        if pyglet.window.key.F in pks and debug:
            self.reset_ball("debug")
        for p in self.players:
            p.last_movements.pop(0)
            if p.up_key in pks and p.down_key not in pks:
                p.y -= self.speed
                p.last_movements.append(-self.speed)
            elif p.up_key not in pks and p.down_key in pks:
                p.y += self.speed
                p.last_movements.append(+self.speed)
            else:
                # notice how we popped from _place_ zero,
                # but append _a number_ zero here. it's not the same.
                p.last_movements.append(0)
        self.update_ball()


class Controller(object):

    def __init__(self, model):
        self.m = model

    def on_key_press(self, symbol, modifiers):
        # `a |= b`: mathematical or. add to set a if in set a or b.
        # equivalent to `a = a | b`.
        # XXX p0 holds down both keys => p1 controls break  # PYGLET!? D:
        self.m.pressed_keys |= set([symbol])

    def on_key_release(self, symbol, modifiers):
        if symbol in self.m.pressed_keys:
            self.m.pressed_keys.remove(symbol)

    def update(self):
        self.m.update()


class View(object):

    def __init__(self, window, model):
        self.w = window
        self.m = model
        # ------------------ IMAGES --------------------#
        # "white_square.png" is a 10x10 white image
        lplayer = pyglet.resource.image("white_square.png")
        # TODO stack the same graphic together three times?
        self.player_spr = pyglet.sprite.Sprite(lplayer)

    def redraw(self):
        # ------------------ PLAYERS --------------------#
        TO_SIDE = self.m.ball.TO_SIDE
        for p in self.m.players:
            self.player_spr.x = p.x//1 - TO_SIDE
            # oh god! pyglet's (0, 0) is bottom right! madness.
            self.player_spr.y = self.w.height - (p.y//1 + TO_SIDE)
            self.player_spr.draw()  # these 3 lines: pretend-paddle
            self.player_spr.y -= 2*TO_SIDE; self.player_spr.draw()
            self.player_spr.y += 4*TO_SIDE; self.player_spr.draw()
        # ------------------ BALL --------------------#
        self.player_spr.x = self.m.ball.x//1 - TO_SIDE
        self.player_spr.y = self.w.height - (self.m.ball.y//1 + TO_SIDE)
        self.player_spr.draw()
        # TODO draw scores (either as label or 3-by-5 pixel graphic)


class Window(pyglet.window.Window):

    def __init__(self, *args, **kwargs):
        DIM = (800, 450)  # DIMENSIONS
        super(Window, self).__init__(width=DIM[0], height=DIM[1],
                                     *args, **kwargs)
        # ------------------ MVC --------------------#
        the_window = self
        self.model = Model(DIM)
        self.view = View(the_window, self.model)
        self.controller = Controller(self.model)
        # ------------------ CLOCK --------------------#
        fps = 30.0
        pyglet.clock.schedule_interval(self.update, 1.0/fps)
        pyglet.clock.set_fps_limit(fps)

    def on_key_release(self, symbol, modifiers):
        self.controller.on_key_release(symbol, modifiers)

    def on_key_press(self, symbol, modifiers):
        self.controller.on_key_press(symbol, modifiers)

    def update(self, *args, **kwargs):
        # XXX make more efficient (save last position, draw black square
        # over that and the new square, don't redraw _entire_ frame.)
        self.clear()
        self.controller.update()
        self.view.redraw()


def main():
    if debug: print("init window...")
    window = Window()
    if debug: print("done! init app...")
    pyglet.app.run()


debug = 1

if __name__ == "__main__":
    main()
