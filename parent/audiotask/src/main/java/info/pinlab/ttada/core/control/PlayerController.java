package info.pinlab.ttada.core.control;

import info.pinlab.pinsound.app.AudioPlayer;
import info.pinlab.pinsound.app.AudioPlayerListener;
import info.pinlab.pinsound.app.AudioPlayerView;
import info.pinlab.ttada.core.model.rule.AudioRule;

public class PlayerController implements AudioPlayController, AudioPlayerListener{
		private AudioPlayer player;
		private AudioRule arule = null;
		private AudioPlayerView view = null;
		private Object model = null;
		private Runnable afterPlayHook = null;
		int audioPlayN = 0;
		
		public PlayerController(AudioPlayer player){
			setAudioPlayer(player);
		}
		public void setView(AudioPlayerView view){
			this.view = view;
			if(player!=null){
				player.setAudioPlayerView(this.view);
			}
			view.setPlayActionListener(this);
		}
		
		public void setModel(Object model){
			this.model = model;
		}
		public Object getModel(){
			return model;
		}
		
		public boolean isPlaying(){
			if(player!=null && player.isPlaying()){
				return true;
			}
			return false;
		}
		
		public void setAfterPlayHook(Runnable run){
			afterPlayHook = run;
			if(player!=null){
				player.setAfterPlayHook(run);
			}
		}
		
		
		@Override
		public void setAudioPlayer(AudioPlayer player) {
			this.player = player;
			if(view!=null){
				view.setPlayActionListener(player);
				player.setAudioPlayerView(view);
			}
			if(afterPlayHook!=null){
				player.setAfterPlayHook(afterPlayHook);
			}
		}
		
		
		

		@Override
		public AudioPlayer getAudioPlayer() {
			return player;
		}

		@Override
		public void setAudioPlayerView(AudioPlayerView view){
			if(view==null){
				return;
			}
			this.view = view;
			
			if(player != null){
				player.setAudioPlayerView(this.view);
				this.view.setPlayActionListener(this);
			}
			
		}

		@Override
		public void setAudioRule(AudioRule arule) {
			this.arule = arule;
		}
		public void pausePlaying(){
			if (player!=null){
				if (player.isPlaying()){
					player.reqPauseToggle();  //-- pauses if it's playing
				}
			}
		}
		
		@Override
		public void reqStop() {
			if(player == null) {
				view.setReadyToPlayState(); 
				return;
			}
//			playerView.setPlayingState();
			//-- can't pause / stop by rule 
			if(arule != null &&  !arule.canPause ) {
				view.setReadyToPlayState(); 
				return;
			}
			//-- already stopped
//			if(!player.isPlaying()){
//				playerView.setReadyToPlayState();
//				return;
//			}
			player.reqStop();
		}
		
		@Override
		public void reqPlay() {
			if(player == null){//-- no player 
				view.setReadyToPlayState(); 
				return; 
			}
			//-- too many plays
			if(arule != null && arule.maxPlayN > -1 && audioPlayN >= arule.maxPlayN ) {
				TaskControllerWithAudio.logger.debug("Can't play because of too many plays! " + audioPlayN + ">" + arule.maxPlayN);
				view.setReadyToPlayState();
				return; 
			}
			//-- just can't play audio by rule
			if(arule != null && !arule.canPlay ){
				TaskControllerWithAudio.logger.debug("Can't play because of AudioRule is set to canPlay==false");
				view.setReadyToPlayState(); 
				return; 
			}
			//-- already playing
			if(player.isPlaying()){
				view.setPlayingState();
				return;
			}
			audioPlayN++;
			player.reqPlay();
			
			
		}
		
		@Override
		public void reqPauseToggle(){
			if(player == null) {
				TaskControllerWithAudio.logger.debug("Can't toggle pause/play because no player is set!");
				return;
			}
			if(player.isPlaying()){
				//-- just can't pause audio
				if(arule != null &&  ! arule.canPause ){ 
					view.setPlayingState();
					TaskControllerWithAudio.logger.debug("Can't toggle pause/play because audio rule doesn't allow (canPause=false)!");
					return;
				}
				player.reqPauseToggle();
			}else{ //-- not playing
				this.reqPlay();
			}
		}
		
		@Override
		public void reqPosInMs(long ms){
			if(player!=null){
				player.reqPosInMs(ms);
			}
		}
		
		public void onViewVisible(){
			final long startT = System.currentTimeMillis();
//			System.out.println("Visible!!!" + arule.delay + "\t" + this.player + "\t" + arule.maxPlayN);
			if (player==null || arule==null 
					|| (arule.maxPlayN>0 && this.audioPlayN  >= arule.maxPlayN)){
				return ;
			}

			if(arule.delay>=0){
				Runnable delayedPlay = new Runnable() {
					@Override
					public void run() {
						long deltaT = System.currentTimeMillis() - startT;
						while(deltaT < arule.delay){
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							deltaT = System.currentTimeMillis() - startT;
						}
						player.reqPlay();
					}
				};
				new Thread(delayedPlay).start();
			};
		}
		
	}