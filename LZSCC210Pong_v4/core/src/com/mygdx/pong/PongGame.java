package com.mygdx.pong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.UI.InventoryUI;
import com.mygdx.helpers.FancyFontHelper;
import com.mygdx.helpers.ScreenType;
import com.mygdx.objects.Player;
import com.mygdx.screens.EndGameScreen;
import com.mygdx.screens.GameScreen;
import com.mygdx.screens.InfoScreen;
import com.mygdx.screens.MenuScreen;


/**
 * A singleton class representing the game. It takes care of
 * - initialising the game
 * - moving among different screens
 * Other classes can call the instance of this class via PongGame.getInstance()
 */
public class PongGame extends Game {

	private static PongGame INSTANCE = null;
	
	private int windowWidth, windowHeight;
	
	private OrthographicCamera ortographicCamera;
	
	private Player player;

	private PongGame() {
		INSTANCE = this;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public static PongGame getInstance() {
		if(INSTANCE == null)
			INSTANCE = new PongGame();
		
		return INSTANCE;
	}
	
	public void createForTest(int width, int height) {
		this.windowHeight = width;
		this.windowHeight = height;
	}
	
	@Override
	public void create () {
		this.windowWidth = Gdx.graphics.getWidth();
		this.windowHeight = Gdx.graphics.getHeight();
		this.ortographicCamera = new OrthographicCamera();
		this.ortographicCamera.setToOrtho(false, this.windowWidth, this.windowHeight);
		
		// MenuScreen is the starting screen of the game
		setScreen(new MenuScreen());
	}

	// Getter methods for windows width and height
	public int getWindowWidth() {
		return windowWidth;
	}


	public int getWindowHeight() {
		return windowHeight;
	}
	
	// Two methods for moving among different screens
	public void changeScreen(Screen currentScreen, ScreenType newScreenType) {
		
		if(newScreenType == ScreenType.GAME)
			setScreen(new GameScreen(this.ortographicCamera));
		if(newScreenType == ScreenType.MENU)
			setScreen(new MenuScreen());
		if(newScreenType == ScreenType.INFO)
			setScreen(new InfoScreen());
	
		if(newScreenType == ScreenType.INVENTORY) {
			// Use the stored player instead of null
			if (player != null) {
				setScreen(new InventoryUI(this, player));
			} else {
				setScreen(new InventoryUI(this, null));
			}
		}
	}
	

	// Exit the game
	public void exit(Screen screen) {
		FancyFontHelper.getInstance().dispose();
		
		Gdx.app.exit();
	}
	
}
