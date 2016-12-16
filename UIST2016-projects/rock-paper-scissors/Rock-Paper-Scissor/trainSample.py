################################################################################
# Copyright (C) 2012-2013 Leap Motion, Inc. All rights reserved.               #
# Leap Motion proprietary and confidential. Not for distribution.              #
# Use subject to the terms of the Leap Motion SDK Agreement available at       #
# https://developer.leapmotion.com/sdk_agreement, or another agreement         #
# between Leap Motion and you, your company or other organization.             #
################################################################################
import sys
currentPath = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, currentPath + "/LeapDeveloperKit_2.3.1+31549_mac/LeapSDK/lib")

import Leap, sys, thread, time
from Leap import CircleGesture, KeyTapGesture, ScreenTapGesture, SwipeGesture

import os
import numpy as np
import pylab as pl
import glob
import cv2
import subprocess
import math
import re
import pickle
from sklearn.externals import joblib
from sklearn.cross_validation import train_test_split
from sklearn.grid_search import GridSearchCV
from sklearn.metrics import classification_report
from sklearn.svm import SVC
from sklearn.svm import LinearSVC

class SampleListener(Leap.Listener):
    finger_names = ['Thumb', 'Index', 'Middle', 'Ring', 'Pinky']
    bone_names = ['Metacarpal', 'Proximal', 'Intermediate', 'Distal']
    state_names = ['STATE_INVALID', 'STATE_START', 'STATE_UPDATE', 'STATE_END']

    def on_init(self, controller):
        #print "Initialized"
        print ""

    def on_connect(self, controller):
        #print "Connected"
        # Enable gestures
        controller.enable_gesture(Leap.Gesture.TYPE_CIRCLE);
        controller.enable_gesture(Leap.Gesture.TYPE_KEY_TAP);
        controller.enable_gesture(Leap.Gesture.TYPE_SCREEN_TAP);
        controller.enable_gesture(Leap.Gesture.TYPE_SWIPE);

    def on_disconnect(self, controller):
        # Note: not dispatched when running in a debugger.
        #print "Disconnected"
        print ""
    def on_exit(self, controller):
        #print "Exited"
        print ""

    def on_frame(self, controller):
        # Get the most recent frame and report some basic information
        frame = controller.frame()
        """
        print "Frame id: %d, timestamp: %d, hands: %d, fingers: %d, tools: %d, gestures: %d" % (
              frame.id, frame.timestamp, len(frame.hands), len(frame.fingers), len(frame.tools), len(frame.gestures()))
        """
        samples = []
        sample = []
        thumbP = []
        cosP = []

        # Get hands
        for hand in frame.hands:

            handType = "Left hand" if hand.is_left else "Right hand"
            """
            print "  %s, id %d, position: %s" % (
                handType, hand.id, hand.palm_position)
            """
            # Get the hand's normal vector and direction
            normal = hand.palm_normal
            direction = hand.direction

            # Calculate the hand's pitch, roll, and yaw angles
            """print "  pitch: %f degrees, roll: %f degrees, yaw: %f degrees" % (
                direction.pitch * Leap.RAD_TO_DEG,
                normal.roll * Leap.RAD_TO_DEG,
                direction.yaw * Leap.RAD_TO_DEG)
            """
            cosP.append(math.cos(direction.pitch * Leap.RAD_TO_DEG))
            cosP.append(math.cos(normal.roll * Leap.RAD_TO_DEG))
            cosP.append(math.cos(direction.yaw * Leap.RAD_TO_DEG))

            # Get arm bone
            arm = hand.arm
            """
            print "  Arm direction: %s, wrist position: %s, elbow position: %s" % (
                arm.direction,
                arm.wrist_position,
                arm.elbow_position)
            """

            # Get fingers
            for finger in hand.fingers:
                """
                print "    %s finger, id: %d, length: %fmm, width: %fmm" % (
                    self.finger_names[finger.type],
                    finger.id,
                    finger.length,
                    finger.width)
                """

                # Get bones
                for b in range(0, 4):
                    bone = finger.bone(b)
                    """
                    print "      Bone: %s, start: %s, end: %s, direction: %s" % (
                        self.bone_names[bone.type],
                        bone.prev_joint,
                        bone.next_joint,
                        bone.direction)
                    """
                    if b==0:
                        thumbP.append(bone.prev_joint[0])
                        thumbP.append(bone.prev_joint[1])
                        thumbP.append(bone.prev_joint[2])
                    else:
                        for i in range(0,3):
                            prevP = (bone.prev_joint[i]-thumbP[i])/finger.length*cosP[i]
                            nextP = (bone.next_joint[i]-thumbP[i])/finger.length*cosP[i]
                            sample.append(prevP)
                            sample.append(nextP)

                            if(sys.argv[1]=="--train"):
                                print "%f %f" % (prevP,nextP)
                                
        # Get tools
        for tool in frame.tools:
            #print "tool"
            """
            print "  Tool id: %d, position: %s, direction: %s" % (
                tool.id, tool.tip_position, tool.direction)
            sample.append(tool.id)
            for i in range(0,3):
                sample.append(tool.tip_position[i])
                sample.append(tool.direction[i])
            """
        # Get gestures
        for gesture in frame.gestures():
            #print "gestures"
            if gesture.type == Leap.Gesture.TYPE_CIRCLE:
                circle = CircleGesture(gesture)

                # Determine clock direction using the angle between the pointable and the circle normal
                if circle.pointable.direction.angle_to(circle.normal) <= Leap.PI/2:
                    clockwiseness = "clockwise"
                else:
                    clockwiseness = "counterclockwise"

                # Calculate the angle swept since the last frame
                swept_angle = 0
                if circle.state != Leap.Gesture.STATE_START:
                    previous_update = CircleGesture(controller.frame(1).gesture(circle.id))
                    swept_angle =  (circle.progress - previous_update.progress) * 2 * Leap.PI
                """
                print "  Circle id: %d, %s, progress: %f, radius: %f, angle: %f degrees, %s" % (
                        gesture.id, self.state_names[gesture.state],
                        circle.progress, circle.radius, swept_angle * Leap.RAD_TO_DEG, clockwiseness)
                print "%d %s %f %f %f %s" % (
                        gesture.id, self.state_names[gesture.state],
                        circle.progress, circle.radius, swept_angle * Leap.RAD_TO_DEG, clockwiseness)
                sample.append(gesture.id)
                sample.append(circle.progress)
                sample.append(circle.radius)
                sample.append(swept_angle * Leap.RAD_TO_DEG)
                for i in range(0,3):
                    sample.append(self.state_names[gesture.state][i])
                    sample.append(clockwiseness[i])
                """

            if gesture.type == Leap.Gesture.TYPE_SWIPE:
                swipe = SwipeGesture(gesture)
                """
                print "  Swipe id: %d, state: %s, position: %s, direction: %s, speed: %f" % (
                        gesture.id, self.state_names[gesture.state],
                        swipe.position, swipe.direction, swipe.speed)
                print "%d %s %s %s %f" % (
                        gesture.id, self.state_names[gesture.state],
                        swipe.position, swipe.direction, swipe.speed)
                sample.append(gesture.id)
                sample.append(swipe.speed)
                for i in range(0,3):
                    sample.append(self.state_names[gesture.state][i])
                    sample.append(swipe.position[i])
                    sample.append(swipe.direction[i])
                """
            if gesture.type == Leap.Gesture.TYPE_KEY_TAP:
                keytap = KeyTapGesture(gesture)
                """
                print "  Key Tap id: %d, %s, position: %s, direction: %s" % (
                        gesture.id, self.state_names[gesture.state],
                        keytap.position, keytap.direction )
                print "%d %s %s %s" % (
                        gesture.id, self.state_names[gesture.state],
                        keytap.position, keytap.direction )
                sample.append(gesture.id)
                for i in range(0,3):
                    sample.append(self.state_names[gesture.state][i])
                    sample.append(keytap.position[i])
                    sample.append(keytap.direction[i])
                """
            if gesture.type == Leap.Gesture.TYPE_SCREEN_TAP:
                screentap = ScreenTapGesture(gesture)
                """
                print "  Screen Tap id: %d, %s, position: %s, direction: %s" % (
                        gesture.id, self.state_names[gesture.state],
                        screentap.position, screentap.direction )
                print "%d %s %s %s" % (
                        gesture.id, self.state_names[gesture.state],
                        screentap.position, screentap.direction )
                sample.append(gesture.id)
                sample.append()
                for i in range(0,3):
                    sample.append(self.state_names[gesture.state][i])
                    sample.append(screentap.position[i])
                    sample.append(screentap.direction[i])
                """
        if not (frame.hands.is_empty and frame.gestures().is_empty):
            print ""

        if(sys.argv[1]=="--test"):
            samples.insert(0,sample)
            enumm = dict({0:"paper",1:"scissor",2:"stone"})
            svc = joblib.load('model.pkl')

            print "Predict: ",[enumm[i] for i in svc.predict(samples)]

    def state_string(self, state):
        if state == Leap.Gesture.STATE_START:
            return "STATE_START"

        if state == Leap.Gesture.STATE_UPDATE:
            return "STATE_UPDATE"

        if state == Leap.Gesture.STATE_STOP:
            return "STATE_STOP"

        if state == Leap.Gesture.STATE_INVALID:
            return "STATE_INVALID"

def main():

    # Create a sample listener and controller
    listener = SampleListener()
    controller = Leap.Controller()

    # Have the sample listener receive events from the controller
    controller.add_listener(listener)

    # Keep this process running until Enter is pressed
    #print "Press Enter to quit..."
    try:
        sys.stdin.readline()
    except KeyboardInterrupt:
        pass
    finally:
        # Remove the sample listener when done
        controller.remove_listener(listener)


if __name__ == "__main__":
    main()
