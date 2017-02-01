using UnityEngine;
using System.Collections;

public class HUD : MonoBehaviour {

	public GameObject playerL;
	public GameObject playerR;

	// Use this for initialization
	void Start () {
	
		//TextMesh t = (TextMesh)gameObject.GetComponent(typeof(TextMesh));
	}
	
	// Update is called once per frame
	void Update () {

		if (playerL != null && playerR != null) {
			//Debug.Log (playerL.GetComponent<Points> ().points);
			//Debug.Log (playerR.GetComponent<Points> ().points);
			string str = playerL.GetComponent<Points> ().points.ToString () + ":" + playerR.GetComponent<Points> ().points.ToString ();
			GetComponent<TextMesh> ().text = str;
		}
	
	}
}
