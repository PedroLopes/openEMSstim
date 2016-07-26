using UnityEngine;
using System.Collections;

public class LostPoint : MonoBehaviour {

	public GameObject other_player;
	public GameObject ball;
	public openEMSstim openEMSstim;
	public string command_to_stimulate_this_player = "C0I100T1000G";
	private byte[] message_to_stimulate_this_player; 

	void Start () {
		message_to_stimulate_this_player = System.Text.Encoding.UTF8.GetBytes(command_to_stimulate_this_player);
	}
		
	void OnTriggerEnter2D(Collider2D other) {
		Debug.Log ("point to" + other_player);
		if (other_player != null) {
			other_player.GetComponent<Points> ().points += 1;
			openEMSstim.sendMessage (message_to_stimulate_this_player);
		}

		if (ball != null) { 
			ball.transform.position = new Vector2 (0, 0);
			Vector2 dir = new Vector2(ball.GetComponent<Rigidbody2D>().velocity.x * -1, 0).normalized;
			ball.GetComponent<Rigidbody2D>().velocity = dir * 10;
		}


	}

}
