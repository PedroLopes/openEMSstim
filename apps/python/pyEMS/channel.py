from preset import Preset

class Channel:

    def __init__(self,channel,intensity, name):
        self.channel_number_on_EMS_machine = channel
        self.intensity = intensity
        self.name = name
        self.presets = []
    
    def search_preset(self,preset_name):
        for idx, p in enumerate(self.presets):
            if p.name == preset_name:
                return True
            else:
                return False
    
    def set_channel(self,channel):
        self.channel_number_on_EMS_machine = channel

    def set_parameters(self,intensity, name):
        self.channel_number_on_EMS_machine = channel
        self.intensity = intensity
        self.name = str(name)

    def set_name(self,name):
        self.name = str(name)

    def add_preset(self,preset):
        if self.search_preset(preset.name) == False:
            self.presets.append(p)
        else:
            print("Warning while adding preset: " + preset.name + " duplicate in Channel " + self.name + " , will not add again with same name.")

    def remove_preset(self,preset):
        if search_preset(preset.name) == True:
            self.presets.remove(preset)
        else:
            print("Warning while removing preset: " + preset.name + " not found in Channel " + self.name)

    def activate_preset(self,preset):
        if search_preset(preset.name) == True:
            self.intensity = preset.intensity
            self.active_preset = preset.name 
        else:
            print("Warning while activating preset: " + preset.name + " not found in Channel " + self.name) 

    def set_intensity(self,intensity):
        self.intensity = intensity

