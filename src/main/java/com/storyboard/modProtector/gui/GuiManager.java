package com.storyboard.modProtector.gui;

import com.storyboard.modProtector.ModProtector;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiManager {

    private static final int ENTRY_BTN_ID = 7000;

    private ModProtector mod;

    public GuiManager(ModProtector mod) {
        this.mod = mod;
    }

    @SubscribeEvent
    public void onGuiInit(InitGuiEvent.Post e) {
        if (e.getGui() instanceof GuiOptions) {
            e.getButtonList().add(new GuiButton(ENTRY_BTN_ID, e.getGui().width - 100, e.getGui().height - 22, 98, 20, "ModProtector"));
        }
    }

    @SubscribeEvent
    public void onGuiAction(ActionPerformedEvent.Post e) {
        if (e.getGui() instanceof GuiOptions && e.getButton().id == ENTRY_BTN_ID) {
            mod.getClient().displayGuiScreen(new ProtectorOptionGui(e.getGui(), mod.getConfigManager()));
        }
    }

}