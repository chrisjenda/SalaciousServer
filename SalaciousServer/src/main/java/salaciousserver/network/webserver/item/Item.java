package salaciousserver.network.webserver.item;

import zombie.core.textures.Texture;

import static zombie.core.textures.Texture.getTexture;

public class Item {
    public final String basename;
    private String displayname;
    public final String tooltip;

    public Item(String basename, String displayname, String tooltip) {
        this.basename = basename;
        this.displayname = displayname;
        this.tooltip = tooltip;
    }

    public String getBasename() { return basename; }

    public String getDisplayname() {
        return displayname;
    }

    public String getTooltip() { return tooltip; }

}