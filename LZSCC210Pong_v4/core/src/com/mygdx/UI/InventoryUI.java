package com.mygdx.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.helpers.FancyFontHelper;
import com.mygdx.helpers.ScreenType;
import com.mygdx.objects.Player;
import com.mygdx.pong.PongGame;

import java.util.Map;


public class InventoryUI extends ScreenAdapter {
    private PongGame game;
    private Player player;
    private SpriteBatch batch;
    private BitmapFont title;
    private BitmapFont itemText;
    
    private Stage stage;
    private Table rootTable;
    
    private Texture inventoryHUDTexture;
    private Image inventoryBackground;
    


    public InventoryUI(PongGame game, Player player) {
        this.game = game;
        this.player = player;
        this.batch = new SpriteBatch();
    }


    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act(delta);
        stage.draw();
        
        // Check for input to close inventory
        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            game.changeScreen(this, ScreenType.GAME);
        }
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (stage != null) stage.dispose();
    }
}