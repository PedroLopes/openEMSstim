## bioSync: Wearable Muscle I/O Device for Blending Kinesthetic Experience among People 

This work presents a new style of kinesthetic interpersonal communication. The users are able to perceive muscle activity bi-directionally, such as muscle contraction or rigidity of joints, through somatosensory channels in a realistic manner.
To achieve bi-directional kinesthetic interaction, we have developed a wearable muscle I/O device, called bioSync, that equips a developed electrode system for enabling the same electrodes to perform biosignal measurement and stimulation. The stimulus frequency is adjustable between 1Hz-100Hz.

We conducted a pilot study to evaluate the optimal forms of a feeding spoon for people with neuromuscular disorders by reproducing muscle tremors in healthy people. Potential scenarios include interactive rehabilitations and sports training. It is essential for both the trainers and the learners to perceive not only the physical bodily motions but also the muscle activity.

![overview](extra/images/overview.png)

## System Architecture
This shows the system configuration of the developed device. It consists of three electrodes, discharge switches, gate switches, a stimulation circuit, an EMG measurement circuit, and a microprocessor with a Bluetooth module.

![architecture](extra/images/architecture.png)

## Simultaneous Operation of EMG and EMS
To achieve fast and simultaneous measurement and stimulation operations using common electrodes, a gate switching mechanism and a mechanism for discharging residual potential (the body retains a net charge following the stimulus) are required. The former is designed for protecting the measurement circuit from the stimulus voltage, and the latter is used for modifying connection path of the electrodes. One conventional discharging method is to short each electrode after stimulation in order to discharge the naturally existing capacitors of body. In the proposed method, a ground(0V) voltage connection following the electrode shorts is established using the discharge switch. This allows faster discharge of the residual potential, and stabilizes measurements immediately after the stimulus.
The process sequence is as follows: 1) the electrodes are connected to the input ports of the measurement circuit by the gate switches; 2) after the measurement, the electrodes are detached from the measurement circuit and connected to the stimulation circuit by the gate switches when the stimulus ends, the electrodes are detached again, and discharging switches are activated for 10ms. This type of  simultaneous operation, using a fewer number of electrodes, allows for reducing the size of device and facilitating an electrode array configuration.

We also propose a method of EMS having dynamic adjustability of the stimulus frequency. The stimulation cycle can be adjusted from 1Hz to 100Hz, thereby enabling dynamic adaption to skin conditions. The pulse amplitude is 27V provided by a DC/DC converter from a 3.7V Li-Po battery; the duration can be adjusted from 0us to 700us. The EMG measurement circuit consists of an input protection mechanism, a voltage follower for impedance adjustment, a differential amplifier, a second-order RC low pass filter, and a microprocessor with a bluetooth module. 

![config](extra/images/Configuration.png)

## Concrete Scenarios
### Sharing Embodied Experience for Product Design
Tremors and rigidity in the upper and lower limbs are two of the major symptoms of Parkinson's disorder. The disease affects the activities in daily life (ADL) and the quality of life (QOL) of patients and their families. Therefore, it is very important for the therapists and family members to understand the physical challenges faced by the patients in their daily lives. 
bioSync can help recreate physiological muscular tremors and transfer these symptoms from patients to healthy people, thus enabling the demo visitors to experience virtual and realistic three-dimensional tremors by fully employing visitor's musculo-skeletal system while preserving natural embodied interaction.

### Interactive Clinical Gait Training
Gait training with an exoskeletal robot for paraplegic patients has been widely investigated. It is important to learn the timing of each gate phase such as backward kick-out in gait training. bioSync enables a patient and a therapist to share the timings for such exertions, thereby creating the possibility for enhanced monitoring and interactive teaching.

### [New] Assisting the Hand Interactions with Tools
bioSync is able to both measure and stimulate the user's muscle simultaneously. Using this characteristics, the device can tell the user an appropriate grasping force for a tool. When the user's grasping force is not enough, the bioSync stimulates the user's hand to induce to hold the tool tightly, by calculating the gap between measured grasping force (estimated by measured EMG values) and required grasping force (preprogrammed). When the user's grasping force reaches an appropriate level, the stimulus become week and the user can perceive that his applied force to the tool is appropriate through their somatosensory feedback.

![scenarios](extra/images/scenarios.png)

### Author of bioSync
* Jun Nishida <jun.nishida@acm.org> (hardware, middleware, software, cases)

### Authors of openEMSstim
* Pedro Lopes <plopesresearch@gmail.com> (software, cases, hardware remix, videos)
* Doğa Yüksel <dogayuksel@gmail.com> (cases)
* Sijing You (testing)
* copyright 2016 by Tim Dünte <tim.duente@hci.uni-hannover.de>
* copyright 2016 by Max Pfeiffer <max.pfeiffer@hci.uni-hannover.de>

### Other Materials
* Jun Nishida and Kenji Suzuki. 2016. bioSync: Synchronous Kinesthetic Experience among People. In Proceedings of the 2016 CHI Conference Extended Abstracts on Human Factors in Computing Systems (CHI EA '16). ACM, New York, NY, USA, 3742-3745. DOI:
http://dx.doi.org/10.1145/2851581.2890244
* Jun Nishida, Kenji Suzuki. 2016. bioSync: Wearable haptic I/O device for synchronous kinesthetic interaction. In Proceedings of IEEE Virtual Reality (VR), pp.243-244. DOI:
http://dx.doi.org/10.1109/VR.2016.7504744
* Jun Nishida, Kanako Takahashi, and Kenji Suzuki. 2015. A wearable stimulation device for sharing and augmenting kinesthetic feedback. In Proceedings of the 6th Augmented Human International Conference (AH '15). ACM, New York, NY, USA, 211-212. DOI:
http://dx.doi.org/10.1145/2735711.2735775

### References
* Scott Brave and Andrew Dahley. 1997. inTouch: a medium for haptic interpersonal communication. In CHI '97 Extended Abstracts on Human Factors in Computing Systems (CHI EA '97). ACM, New York, NY, USA, 363-364. DOI:
http://dx.doi.org/10.1145/1120212.1120435
* Ken Nakagaki, Chikara Inamura, Pasquale Totaro, Thariq Shihipar, Chantine Akikyama, Yin Shuang, and Hiroshi Ishii. 2015. Linked-Stick: Conveying a Physical Experience using a Shape-Shifting Stick. In Proceedings of the 33rd Annual ACM Conference Extended Abstracts on Human Factors in Computing Systems (CHI EA '15). ACM, New York, NY, USA, 1609-1614. DOI:
http://dx.doi.org/10.1145/2702613.2732712
* Shunichi Kasahara and Jun Rekimoto. 2014. JackIn: integrating first-person view with out-of-body vision generation for human-human augmentation. In Proceedings of the 5th Augmented Human International Conference (AH '14). ACM, New York, NY, USA, , Article 46 , 8 pages. DOI:
http://dx.doi.org/10.1145/2582051.2582097
* Pedro Lopes, Alexandra Ion, Willi Mueller, Daniel Hoffmann, Patrik Jonell, and Patrick Baudisch. 2015. Proprioceptive Interaction. In Proceedings of the 33rd Annual ACM Conference on Human Factors in Computing Systems (CHI '15). ACM, New York, NY, USA, 939-948. DOI:
http://dx.doi.org/10.1145/2702123.2702461
* Emi Tamaki, Takashi Miyaki, and Jun Rekimoto. 2011. PossessedHand: techniques for controlling human hands using electrical muscles stimuli. In Proceedings of the SIGCHI Conference on Human Factors in Computing Systems (CHI '11). ACM, New York, NY, USA, 543-552. DOI: 
http://dx.doi.org/10.1145/1978942.1979018
