# Choosing the right TENS device for this board

Like most amplifiers this EMS control board looses some signal intensity. Which means you need to feed it with a strong enough EMS signal to produce actuation on the user's muscles. 

Beware that some TENS machines (specially the really small ones, often powered by button cells instead of 9v blocks) will not provide sufficient current (milliamps) for you to have a compelling actuation on the user. Avoid those and instead try to search for a medically compliant (check regulation of your country) EMS machine. 

I personally prefer analog machines to the digital ones. Anyway, the pulse of your machine might look something like a square waveform (with an up pulse and a down pulse, that's important) or a  sharp sine that slowly decays later (a modulated square wave with a faster and higher attack). The typical TENS machines we tested so far range up to 70mA or 100mA. You won't need all that power to actuate a muscle though. 

Here's a list of machines we have tested with this system (if you have new ones please submit through GitHub's pull request):

| Machine       | Actuates ok?   | observations  	| Link | analog or digital? | 
| ------------- |:--------:| ---------:|------------:|
| Sanitas  		|yes 	|very powerful, works just fine 	|http://www.sanitas-online.de/web/_dokumente/GAs/therapie/752.907-0212_SEM43.pdf| digital |
| TNS SM1 (Serie C) 		|yes		|Not powerful enough for very strong actuations or leg muscles. Reported max 70 mA 		| https://www.yumpu.com/de/document/view/2545660/tens-gerat-tns-sm-1-aks-reizstromgerat-schwa-medico | analog |
| TNS SM2 		|yes		|similar as above			|https://www.yumpu.com/de/document/view/2534032/tens-gerat-tns-sm-2-mf-schwa-medico| analog |
