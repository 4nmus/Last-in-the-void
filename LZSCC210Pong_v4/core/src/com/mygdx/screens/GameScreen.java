package com.mygdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.helpers.BodyHelper;
import com.mygdx.helpers.Constants;
import com.mygdx.helpers.ContactType;
import com.mygdx.helpers.FancyFontHelper;
import com.mygdx.helpers.GameContactListener;
import com.mygdx.helpers.ScreenType;
import com.mygdx.objects.Player;
import com.mygdx.objects.SpaceShip;
import com.mygdx.pong.PongGame;

/**
 * This is the main game screen for the space exploration game
 */
public class GameScreen extends ScreenAdapter {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private World world;
    private BitmapFont font;
    
    // Game objects
    private SpaceShip playerShip;
    private Player player;
    
    // Game tracking
    private double health = 100;
    private double fuel = 100;
    private double oxygen = 100;
    
    private boolean inventoryOpen = false;
    private Texture inventoryHUDTexture;
    private SpriteBatch uiBatch;
    private Stage inventoryStage;
    


    public GameScreen(OrthographicCamera camera) {
        Box2D.init();
        
        this.camera = camera;
        this.camera.position.set(new Vector3(PongGame.getInstance().getWindowWidth()/2, 
            PongGame.getInstance().getWindowHeight()/2, 0));
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0, 0), false);
        
        this.world.setContactListener(new GameContactListener(this));
    
        // Initialize player (without passing to GameContactListener)
        this.player = new Player();
        PongGame.getInstance().setPlayer(player);
        
        // Create spaceship
        Body shipBody = BodyHelper.createRectangularBody(
            PongGame.getInstance().getWindowWidth() / 2, 
            PongGame.getInstance().getWindowHeight() / 2, 
            Constants.PLAYER_PADDLE_WIDTH, 
            Constants.PLAYER_PADDLE_HEIGHT,
            BodyType.KinematicBody, 1f, getWorld(), ContactType.PLAYER);
        this.playerShip = new SpaceShip(
            PongGame.getInstance().getWindowWidth() / 2, 
            PongGame.getInstance().getWindowHeight() / 2, 
            shipBody);
        
        this.font = FancyFontHelper.getInstance().getFont(Color.WHITE, 20);

        this.uiBatch = new SpriteBatch();
        loadInventoryTextures();
        setupInventoryUI();
    }
    
    
    public void update() {
        this.world.step(1/60f, 6, 2);
        
        // Update components
        this.camera.update();
        this.playerShip.update();
        
        this.batch.setProjectionMatrix(this.camera.combined);
        
        // Handle input
        handleInput();
        
    }
    
    private void handleInput() {
        // To return to the menu screen
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            PongGame.getInstance().changeScreen(this, ScreenType.MENU);
        // To open the inventory screen
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            inventoryOpen = !inventoryOpen;
            if (inventoryOpen) { 
                pause();
            }
        } 
    }  
    

    @Override
    public void render(float delta) {
        update();
        
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        this.batch.begin();
        // Draw spaceship
        this.playerShip.render(batch);
        // Draw player stats
        drawPlayerStats();
        this.batch.end();
        
        // Draw inventory if open
        if (inventoryOpen) {
            renderInventoryUI();
        }
    }

    
    private void drawPlayerStats() {
        float statsX = 10;
        float statsY = PongGame.getInstance().getWindowHeight() - 20;
        
        BitmapFont statsFont = FancyFontHelper.getInstance().getFont(Color.WHITE, 16);
        
        // Draw player name
        statsFont.draw(batch, "Name: Space Explorer", statsX, statsY);
        
        // Health with color indication
        Color healthColor = getResourceColor(health);
        BitmapFont healthFont = FancyFontHelper.getInstance().getFont(healthColor, 16);
        healthFont.draw(batch, "Health: " + (int)health + "%", statsX, statsY - 20);
        
        // Fuel with color indication
        Color fuelColor = getResourceColor(fuel);
        BitmapFont fuelFont = FancyFontHelper.getInstance().getFont(fuelColor, 16);
        fuelFont.draw(batch, "Fuel: " + (int)fuel + "%", statsX, statsY - 40);
        
        // Oxygen with color indication
        Color oxygenColor = getResourceColor(oxygen);
        BitmapFont oxygenFont = FancyFontHelper.getInstance().getFont(oxygenColor, 16);
        oxygenFont.draw(batch, "Oxygen: " + (int)oxygen + "%", statsX, statsY - 60);
    }
    
    private Color getResourceColor(double value) {
        if (value > 70) return Color.GREEN;
        else if (value > 30) return Color.YELLOW;
        else return Color.RED;
    }
    
    
    // Helper method to create a single pixel texture for stars
    private Texture createPixel() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }



    private void loadInventoryTextures() {
        inventoryHUDTexture = new Texture(Gdx.files.internal("InventoryHUD.png"));
    }

    private void setupInventoryUI() {
        inventoryStage = new Stage(new FitViewport(
            PongGame.getInstance().getWindowWidth(), 
            PongGame.getInstance().getWindowHeight()
        ));
    }

    // Method to open the inventory screen
    private void renderInventoryUI() {
        uiBatch.begin();
        
        float scaledWidth = inventoryHUDTexture.getWidth() * 0.85f;
        float scaledHeight = inventoryHUDTexture.getHeight() * 0.85f;
    
        // Draw inventory background
        uiBatch.draw(inventoryHUDTexture,
            (PongGame.getInstance().getWindowWidth() - scaledWidth) / 2,
            (PongGame.getInstance().getWindowHeight() - scaledHeight) / 2,
            scaledWidth, scaledHeight
            );
        uiBatch.end();

        inventoryStage.act(Gdx.graphics.getDeltaTime());
        inventoryStage.draw();
    }

    // Getter methods
    public World getWorld() {
        return world;
    }
}