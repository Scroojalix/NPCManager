package io.github.scroojalix.npcmanager.utils.chat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TextComponentWrapper {
    
    private TextComponent component;
    private boolean visibleToConsole;
    private boolean isLine;
    
    public TextComponentWrapper(String text, boolean visibleToConsole) {
        component = new TextComponent(text);
        this.visibleToConsole = visibleToConsole;
    }
    
    public TextComponent getComponent() {
        return component;
    }

    public void setIsLine() {
        this.isLine = true;
    }

    public boolean isLine() {
        return isLine;
    }

    public boolean isVisibleToConsole() {
        return this.visibleToConsole;
    }

    public void setText(String text) {
        component.setText(text);
    }

    public String getText() {
        return component.getText();
    }

    public void setColor(ChatColor color) {
        component.setColor(color);
    }

    public ChatColor getColor() {
        return component.getColor();
    }

    public void setBold(boolean bold) {
        component.setBold(bold);
    }

    public void setStrikethrough(boolean strike) {
        component.setStrikethrough(strike);
    }

    public void setClickEvent(ClickEvent event) {
        component.setClickEvent(event);
    }
    
    public void setHoverEvent(HoverEvent event) {
        component.setHoverEvent(event);
    }
}