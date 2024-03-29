package com.example.pro.jukebox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ProJb extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener {
	
	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;
	private ImageButton btnRepeat;
	private ImageButton btnShuffle;
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	// Media Player
	private MediaPlayer mp;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private SongsManager songManager;
	private Utilities utils;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private int currentSongIndex = 0;
	private boolean isShuffle = false;
	private boolean isRepeat = false;
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		
		//All Player Buttons
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnForward = (ImageButton) findViewById(R.id.btnForward);
		btnBackward = (ImageButton) findViewById(R.id.btnBackward);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
		btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
		btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		
		// Mediaplayer
		mp = new MediaPlayer();
		songManager = new SongsManager();
		utils = new Utilities();
		
		//Listeners
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		mp.setOnCompletionListener(this); //Important
		
		//Getting AllSongs list
		songsList= songManager.getPlayList();
		
		//By Default play first song
		playSong(0);
		
		/**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 ***/
		btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//check if song is already playing
				if(mp.isPlaying()){
					if(mp!=null){
						mp.pause();
						//changing button image to play button
						btnPlay.setImageResource(R.drawable.btn_play);						
					}					
				}else{
					//resume song
					if(mp!=null){
						mp.start();
						//changing button to play button
						btnPlay.setImageResource(R.drawable.btn_pause);
					}
				}
			}
		});
		
		/**
		 * Forward button click event
		 * Forwards song specified seconds
		 */
		btnForward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//get current song position
				int currentPosition = mp.getCurrentPosition();
				//check if seekForward time is lesser than song duration
				if(currentPosition + seekForwardTime <= mp.getDuration()){
					//forward song
					mp.seekTo(currentPosition + seekForwardTime);
				}else{
					// forward to end position
					mp.seekTo(mp.getDuration());
				}
			}
		});
		
		/**
		 * Backward button click event
		 * Backward song to specified seconds
		 **/
		btnBackward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// get current song position
				int currentPosition = mp.getCurrentPosition();
				//check if seekBackward time is greater than 0 sec
				if(currentPosition - seekBackwardTime >= 0){
					// forward song
					mp.seekTo(currentPosition - seekBackwardTime);
				}else{
					//backward to starting posistion
					mp.seekTo(0);
				}
			}	
		});
		
		
		/**
		 *Next button click event
		 * Plays next song by taking currentSongIndex + 1 
		 **/
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//check if next song is there or not
				if(currentSongIndex < (songsList.size() - 1)){
					playSong(currentSongIndex + 1);
					currentSongIndex = currentSongIndex + 1;
				}else{
					//play first song
					playSong(0);
					currentSongIndex = 0;
				}
			}
		});
		
		/**
		 * Back bytton click event
		 * Plays previous song by currentSongIndex - 1
		 */
		btnPrevious.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(currentSongIndex > 0){
					playSong(currentSongIndex - 1);
					currentSongIndex = currentSongIndex - 1;
				}else{
					// play last song
					playSong(songsList.size() - 1);
					currentSongIndex = songsList.size() - 1;
				}
			}
			
		});
		
		/**
		 * Button Click event for repeat button
		 * Enables repeat flag to true
		 */
		btnRepeat.setOnClickListener(new View.OnClickListener(){
			
			@Override
			public void onClick(View arg0) {
				if(isRepeat){
					isRepeat = false;
					Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}else{
					//male repeat to true
					isRepeat = true;
					Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
					//make shuffle to false
					isShuffle = false;
					btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				}
			}
			
		});
		
		/**
		 * Button Click event for shuffle button
		 * Enables shuffle flag to true
		 */
		btnShuffle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0){
				if(isShuffle){
					isShuffle = false;
					Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				}else{
					//make repeat to true
					isShuffle= true;
					btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}
			}
			
		});
		
		/**
		 * Button Click event for playlist click event
		 * Launches list activity which displays lists of songs
		 */
		btnPlaylist.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
				startActivityForResult(i, 100);
			}
		});
		
	}
	
	/**
	 * Receiving song index from playlist view
	 * and play the song
	 */
	@Override
	protected void onActivityResult(int requestCode,
									int resultCode, Intent data) {
		super.onActivityResult(requestCode,  resultCode, data);
		if(resultCode == 100){
			currentSongIndex = data.getExtras().getInt("songIndex");
			//play selected song
			playSong(currentSongIndex);			
		}
	}
	
	/**
	 * Function to play a song
	 * @param songIndex - index of song
	 */
	public void playSong(int songIndex){
		//Play Song
		try {
			mp.reset();
			mp.setDataSource(songsList.get(songIndex).get("songPath"));
			mp.prepare();
			mp.start();
			//Displaying song's title
			String songTitle = songsList.get(songIndex).get("songTitle");
			songTitleLabel.setText(songTitle);
			
			// Changing button Image to pause image
			btnPlay.setImageResource(R.drawable.btn_pause);
			
			//set progress bar values
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);
			
			//updating progress bar
			updateProgressBar();			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update timer on seekbar
	 */
	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}
	
	/**
	 * Background Runnable Thread
	 */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long totalDuration = mp.getDuration();
			long currentDuration = mp.getCurrentPosition();
			
			//Displaying total duration time
			songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
			//Displaying time completed with playing
			songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));
			
			//updating progress bar
			int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
			//log.d("Progress", ""+progress; << not sure what this is
			songProgressBar.setProgress(progress);
			
			//Running this thread after 100 milliseconds
			mHandler.postDelayed(this,  100);
		}
	};
	
	/**
	 * 
	 * 
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		
	}
	
	
	/**
	 * when user starts moving the progress handler
	 */
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		//remove message handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);		
	}
	
	/**
	 * when user stops moving the progress handler
	 */
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
		
		//forward or backward to certain seconds
		mp.seekTo(currentPosition);
		
		//update timer progress again
		updateProgressBar();
	}
	
	/**
	 * On Song playing completed
	 * of repeat is on play some song again
	 * if shuffle is on play random song
	 */
	@Override
	public void onCompletion(MediaPlayer arg0) {
		
		// check for repeat is ON OR OFF
		if(isRepeat){
			//repeat is on play same song again
			playSong(currentSongIndex);
		} else if(isShuffle){
			//shuffle is on - play a random song
			Random rand = new Random();
			currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
			playSong(currentSongIndex);
		} else{
			// no repeat or shuffle ON - play next song
			if(currentSongIndex < (songsList.size() - 1)) {
				playSong(currentSongIndex + 1);
				currentSongIndex = currentSongIndex + 1;
			}else{
				//play first song
				playSong(0);
				currentSongIndex = 0;
			}
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mp.release();
	}
	
}


