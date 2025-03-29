package com.mygdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.physics.box2d.Shape;
import com.mygdx.helpers.FancyFontHelper;
import com.mygdx.helpers.ScreenType;
import com.mygdx.pong.PongGame;


public class InventoryScreen extends ScreenAdapter {

    private final PongGame game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer; // i need square around letter
    private BitmapFont title;
    private BitmapFont itemText;
    private BitmapFont instructions;
    
    // Scrolling variables
    private int scrollPosition = 0;
    private final int MAX_SCROLL = 150;
    private final int SCROLL_SPEED = 5;

    private final int ITEM_START_X = 10;
    private final int ITEM_START_Y = 500;
    private final int HEADER_HEIGHT = 80;
        

    public InventoryScreen(PongGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.title = FancyFontHelper.getInstance().getFont(Color.GREEN, 30);
        this.itemText = FancyFontHelper.getInstance().getFont(Color.WHITE, 25);
        this.instructions = FancyFontHelper.getInstance().getFont(Color.WHITE, 15);        
    }   

    @Override
    public void show() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    // top layer is written last
    public void render(float delta) {
        // Handle scrolling input
        handleScrollInput();
        
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        batch.begin();
        this.itemText.draw(batch, getInventoryText(), ITEM_START_X, ITEM_START_Y + scrollPosition);
        batch.end();
        
        // Draw header box
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 1);
        shapeRenderer.rect(0, PongGame.getInstance().getWindowHeight() - HEADER_HEIGHT, PongGame.getInstance().getWindowWidth(), HEADER_HEIGHT);
        shapeRenderer.end();


        batch.begin();
        // title - fixed at the top
        this.title.draw(batch, "Inventory", 20, PongGame.getInstance().getWindowHeight() - 20);
        // instructions - fixed with title
        this.instructions.draw(batch, "Press R to return to the game , UP/DOWN to scroll", 30, PongGame.getInstance().getWindowHeight() - 60);
        batch.end();

        if(Gdx.input.isKeyPressed(Input.Keys.R))
            PongGame.getInstance().changeScreen(this, ScreenType.GAME);
    }

    private void handleScrollInput() {
        // Scroll up
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            int newPosition = scrollPosition + SCROLL_SPEED;
            if (newPosition < MAX_SCROLL){
                scrollPosition = newPosition;
            }
        }   
        // Scroll down
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            int newPosition = scrollPosition - SCROLL_SPEED;
            if (newPosition > 0){
                scrollPosition = newPosition;
            }
        }
    }

    private String getInventoryText() {
        return "Your items:\n"
                + "   Item 1\n"
                + "   Item 2\n"
                + "   Item 3\n"
                + "   Item 4\n"
                + "   Item 5\n"
                + "   Item 6\n"
                + "   Item 7\n"
                + "   Item 8\n"
                + "   Item 9\n"
                + "   Item 10\n"
                + "   Item 11\n"
                + "   Item 12\n"
                + "   Item 13\n"
                + "   Item 14\n"
                + "   Item 15\n";
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
    }
}