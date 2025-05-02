package com.mygdx.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.Align;
import com.mygdx.helpers.FancyFontHelper;
import com.mygdx.objects.Player;
import com.mygdx.pong.PongGame;

import java.util.HashMap;
import java.util.Map;


public class InventoryUI {
    private PongGame game;
    private Player player;
    private SpriteBatch batch;
    private BitmapFont title;
    private BitmapFont itemText;
    
    private Stage stage;
    private Table rootTable;
    
    private TextureAtlas atlas;
    private TextureAtlas itemAtlas;
    private TextureRegionDrawable innerBg;
    private TextureRegionDrawable slotBg;
    
    private Skin skin;
    private Table mainTable;
    private Table inventoryTable;
    private Texture backgroundTexture;
    private NinePatchDrawable panelBackground;
    
    private boolean isVisible = false;
    private String selectedItemName = null;

    private static final Color TITLE_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = Color.LIGHT_GRAY;
    private Map<String, TextureRegionDrawable> itemIcons = new HashMap<>();
    private int inventoryCapacity = 1;


    public InventoryUI(PongGame game, Player player) {
        this.game = game;
        this.player = player;
        
        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        
        this.atlas = new TextureAtlas(Gdx.files.internal("backgrounds.atlas"));
        this.innerBg = new TextureRegionDrawable(atlas.findRegion("GUI_note"));
        this.slotBg = new TextureRegionDrawable(atlas.findRegion("GUI_slot"));
        
        initializePanelBackground();
        createUI();
        loadItemIcons();
    }
    // Getter for inventoryCapacity
    public int getInventoryCapacity() {
        return inventoryCapacity;
    }

    // Setter for inventoryCapacity
    public void setInventoryCapacity(int inventoryCapacity) {
        if (inventoryCapacity >= 0 && inventoryCapacity <=4) { 
            this.inventoryCapacity = inventoryCapacity;
        }
    }
    public interface CloseButtonListener {
        void onCloseButtonClicked();
    }

    private CloseButtonListener closeButtonListener;

    public void setCloseButtonListener(CloseButtonListener listener) {
        this.closeButtonListener = listener;
    }

    private void initializePanelBackground() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.15f, 0.15f, 0.2f, 0.9f);
        pixmap.fill();
        Texture bgTex = new Texture(pixmap);
        panelBackground = new NinePatchDrawable(new NinePatch(bgTex, 0, 0, 0, 0));
        backgroundTexture = bgTex;
        pixmap.dispose();
    }

    private void loadItemIcons() {
        itemAtlas = new TextureAtlas(Gdx.files.internal("resources.atlas"));
        
        // Biomass resources  !! "epic" in atlas = "Legendary" in game !!
        itemIcons.put("Common Biomass", new TextureRegionDrawable(itemAtlas.findRegion("common_biomass")));
        itemIcons.put("Uncommon Biomass", new TextureRegionDrawable(itemAtlas.findRegion("uncommon_biomass")));
        itemIcons.put("Rare Biomass", new TextureRegionDrawable(itemAtlas.findRegion("rare_biomass")));
        itemIcons.put("Legendary Biomass", new TextureRegionDrawable(itemAtlas.findRegion("epic_biomass")));
        
        // Fuel resources
        itemIcons.put("Common Fuel", new TextureRegionDrawable(itemAtlas.findRegion("common_fuel")));
        itemIcons.put("Uncommon Fuel", new TextureRegionDrawable(itemAtlas.findRegion("uncommon_fuel")));
        itemIcons.put("Rare Fuel", new TextureRegionDrawable(itemAtlas.findRegion("rare_fuel")));
        itemIcons.put("Legendary Fuel", new TextureRegionDrawable(itemAtlas.findRegion("epic_fuel")));
        
        // Building Materials resources - note typo in atlas: "buidling" vs "building"
        itemIcons.put("Common Building Materials", new TextureRegionDrawable(itemAtlas.findRegion("common_building_material")));
        itemIcons.put("Uncommon Building Materials", new TextureRegionDrawable(itemAtlas.findRegion("uncommon_buidling_material"))); // Note typo in atlas
        itemIcons.put("Rare Building Materials", new TextureRegionDrawable(itemAtlas.findRegion("rare_buidling_material"))); // Note typo in atlas
        itemIcons.put("Legendary Building Materials", new TextureRegionDrawable(itemAtlas.findRegion("epic_building_material")));
        
        TextureRegionDrawable defaultIcon = new TextureRegionDrawable(itemAtlas.findRegion("defaultIcon"));
        itemIcons.put("default", defaultIcon);
    }
    
    private void createUI() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setBackground(panelBackground);
        mainTable.center(); 
        stage.addActor(mainTable);
        
        Table headerTable = new Table();
        Label titleLabel = new Label("INVENTORY", new Label.LabelStyle(
            FancyFontHelper.getInstance().getFont(TITLE_COLOR, 20), TITLE_COLOR));
        headerTable.add(titleLabel).expandX().left().padLeft(30);

        TextButton closeButton = new TextButton("X", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.setInputProcessor(null);
                isVisible = false;
                mainTable.setVisible(false);
                if (closeButtonListener != null) {
                    closeButtonListener.onCloseButtonClicked();
                }
            }
        });
        headerTable.add(closeButton).size(30, 30).right().padRight(10);
        
        // inventory grid 
        inventoryTable = new Table();
        
        Table panel = new Table();
        panel.setBackground(innerBg);
        panel.add(headerTable).growX().height(50).padTop(10).row();
        panel.add(inventoryTable).grow().pad(20);
        
        // inner window
        mainTable.add(panel).width(750).height(580);
        
        refreshInventoryGrid();
    }
    
    private NinePatchDrawable createPanelBackground(float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new NinePatchDrawable(new NinePatch(texture, 0, 0, 0, 0));
    }
    
    private void refreshInventoryGrid() {
        inventoryTable.clear();
        Map<String, Integer> items = player.getInventory().getItems();
        System.out.println("DEBUG: Inventory items: " + items);
       
        // Create inventory slots
        int index = 0;
        for (int row = 0; row < inventoryCapacity; row++) {
            for (int col = 0; col < 6; col++) {
                // Create slot container
                Table itemContainer = new Table();
                Table slotContainer = new Table();
                slotContainer.setBackground(slotBg);
                slotContainer.setTouchable(Touchable.enabled);            

                // Add item if this slot has one
                if (items != null && index < items.keySet().size()) {
                    String itemName = (String) items.keySet().toArray()[index];
                    int quantity = items.get(itemName);
                    
                    TextureRegionDrawable icon = itemIcons.getOrDefault(itemName, itemIcons.get("missing"));
                    Image iconImage = new Image(icon);
                    slotContainer.add(iconImage).size(48, 48).pad(5);
                    final String currentItemName = itemName;
                    final int currentQuantity = quantity;
                    slotContainer.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            selectedItemName = currentItemName;
                            showRemoveDialog(currentItemName, currentQuantity);
                        }
                    });
                    
                    // Item name (top)
                    String displayString = itemName.length() > 10 ? itemName.substring(0, 6) + "..." : itemName;
                    Label nameLabel = new Label(displayString, new Label.LabelStyle(FancyFontHelper.getInstance().getFont(TEXT_COLOR, 12), TEXT_COLOR));
                    itemContainer.add(nameLabel).center().padBottom(5).row();
                    itemContainer.add(slotContainer).size(65, 65).row();

                    // Quantity (bottom)
                    Label quantityLabel = new Label(String.valueOf(quantity), new Label.LabelStyle(FancyFontHelper.getInstance().getFont(TEXT_COLOR, 12), TEXT_COLOR));
                    itemContainer.add(quantityLabel).bottom().padBottom(5);
                    
                    index++;
                } else {
                    // Empty slots
                    itemContainer.add().height(20).row();
                    itemContainer.add(slotContainer).size(65, 65).row();
                    itemContainer.add().height(20); 
                }

                // Add slot to grid
                inventoryTable.add(itemContainer).width(105).pad(8);
            }
            inventoryTable.row();
        }
    }

    private void showRemoveDialog(String itemName, int maxQuantity) {
        final Dialog dialog = new Dialog("", skin);
        Table content = new Table();
        content.pad(20);
        
        // Item name for removal
        Label titleLabel = new Label("Remove how many [" + itemName + "]?", 
            new Label.LabelStyle(FancyFontHelper.getInstance().getFont(TITLE_COLOR, 16), TITLE_COLOR));
        content.add(titleLabel).colspan(3).padBottom(20).row();
        
        // Quantity selection
        final int[] quantity = {1};
        final Label quantityLabel = new Label("1", skin);
        quantityLabel.setAlignment(Align.center);
        
        // +/- button
        TextButton minusBtn = new TextButton("-", skin);
        minusBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                quantity[0] = Math.max(1, quantity[0] - 1);
                quantityLabel.setText(String.valueOf(quantity[0]));
            }
        });
        
        TextButton plusBtn = new TextButton("+", skin);
        plusBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                quantity[0] = Math.min(maxQuantity, quantity[0] + 1);
                quantityLabel.setText(String.valueOf(quantity[0]));
            }
        });
        
        content.add(minusBtn).width(40).height(40);
        content.add(quantityLabel).width(60).height(40).pad(0, 10, 0, 10);
        content.add(plusBtn).width(40).height(40).row();
        
        Label helpLabel = new Label("to adjust amount", skin);
        content.add(helpLabel).colspan(3).padTop(5).padBottom(20).row();
        
        // confirm/cancel buttons
        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
                selectedItemName = null;
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        Gdx.input.setInputProcessor(stage);
                    }
                });
            }
        });
        
        TextButton confirmButton = new TextButton("Confirm", skin);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                player.getInventory().removeItem(itemName, quantity[0]);
                dialog.hide();
                selectedItemName = null;
                refreshInventoryGrid();
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        Gdx.input.setInputProcessor(stage);
                    }
                });
            }
        });
        
        Table buttonTable = new Table();
        buttonTable.add(confirmButton).width(120).height(40).padRight(20);
        buttonTable.add(cancelButton).width(120).height(40);
        content.add(buttonTable).colspan(3).padTop(20);
        
        dialog.getContentTable().add(content);
        dialog.show(stage);
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    
    public void setVisible(boolean visible) {
        this.isVisible = visible;
        mainTable.setVisible(visible);

        if (visible) {
            refreshInventoryGrid();
            Gdx.input.setInputProcessor(stage);
        } else {
            if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
            }
        }
    }
    
    public void render() {
        if (isVisible) {
            // Draw full-screen darkening background
            stage.getBatch().begin();
            stage.getBatch().setColor(1, 1, 1, mainTable.getColor().a);
            stage.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            stage.getBatch().setColor(1, 1, 1, 1);
            stage.getBatch().end();
            
            // Draw UI elements
            stage.act(Gdx.graphics.getDeltaTime());
            stage.draw();
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public Stage getStage() {
        return stage;
    }
    
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        if (panelBackground != null && panelBackground.getPatch() != null) {
            try {
                panelBackground.getPatch().getTexture().dispose();
            } catch (Exception e) {
                System.err.println("Error disposing: " + e.getMessage());
            }
        }
    }
}
