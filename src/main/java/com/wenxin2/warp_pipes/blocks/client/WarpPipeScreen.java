package com.wenxin2.warp_pipes.blocks.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.init.Config;
import com.wenxin2.warp_pipes.inventory.WarpPipeMenu;
import com.wenxin2.warp_pipes.network.PacketHandler;
import com.wenxin2.warp_pipes.network.SCloseStatePacket;
import com.wenxin2.warp_pipes.network.SPipeBubblesStatePacket;
import com.wenxin2.warp_pipes.network.SWaterSpoutSliderPacket;
import com.wenxin2.warp_pipes.network.SWaterSpoutStatePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.widget.ForgeSlider;

public class WarpPipeScreen extends AbstractContainerScreen<WarpPipeMenu> {
    public static ResourceLocation WARP_PIPE_GUI =
            new ResourceLocation(WarpPipes.MODID, "textures/gui/warp_pipe.png");
    Inventory inventory;
    Button closeButton;
    Button waterSpoutButton;
    Button bubblesButton;
    private double spoutHeight;
    public static ForgeSlider waterSpoutSlider;
    public static ForgeSlider bubblesSlider;

    public WarpPipeScreen(WarpPipeMenu container, Inventory inventory, Component name) {
        super(container, inventory, name);
        this.inventory = inventory;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        Player player = this.inventory.player;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, WARP_PIPE_GUI);

        // Blit format: Texture location, gui x pos, gui y position, texture x pos, texture y pos, texture height, texture width
        final int x = (this.width - this.imageWidth) / 2;
        final int y = (this.height - this.imageHeight) / 2;
        graphics.blit(WARP_PIPE_GUI, x, y, 0, 0, this.imageWidth, this.imageHeight);

        if (this.closeButton.isHoveredOrFocused() && !Config.CREATIVE_CLOSE_PIPES.get())
            graphics.blit(WARP_PIPE_GUI, x + 7, y + 18, 177, 24, 24, 24);
        else if (!player.isCreative() && Config.CREATIVE_CLOSE_PIPES.get())
            graphics.blit(WARP_PIPE_GUI, x + 7, y + 18, 177, 48, 24, 24);
        else graphics.blit(WARP_PIPE_GUI, x + 7, y + 18, 177, 0, 24, 24);

        if (this.waterSpoutButton.isHoveredOrFocused() && !Config.CREATIVE_WATER_SPOUT.get())
            graphics.blit(WARP_PIPE_GUI, x + 34, y + 18, 202, 24, 24, 24);
        else if (!player.isCreative() && Config.CREATIVE_WATER_SPOUT.get())
            graphics.blit(WARP_PIPE_GUI, x + 34, y + 18, 202, 48, 24, 24);
        else graphics.blit(WARP_PIPE_GUI, x + 34, y + 18, 202, 0, 24, 24);

        if (this.bubblesButton.isHoveredOrFocused() && !Config.CREATIVE_BUBBLES.get())
            graphics.blit(WARP_PIPE_GUI, x + 34, y + 45, 227, 24, 24, 24);
        else if (!player.isCreative() && Config.CREATIVE_BUBBLES.get())
            graphics.blit(WARP_PIPE_GUI, x + 34, y + 45, 227, 48, 24, 24);
        else graphics.blit(WARP_PIPE_GUI, x + 34, y + 45, 227, 0, 24, 24);
    }

    @Override
    public void init() {
        super.init();
        final int x = (this.width - this.imageWidth) / 2;
        final int y = (this.height - this.imageHeight) / 2;

        final Component close = Component.translatable("menu.warp_pipes.warp_pipe.close_button");
        this.closeButton = this.addRenderableWidget(new Button.Builder(close, (b) -> {
            this.closeButtonOnPress();
        }).bounds(x + 7, y + 18, 24, 24)
                .tooltip(Tooltip.create(Component.translatable("menu.warp_pipes.warp_pipe.close_button.tooltip")))
                .createNarration(supplier -> Component.translatable("menu.warp_pipes.warp_pipe.close_button.narrate")).build());
        this.closeButton.setAlpha(0);

        final Component waterSpout = Component.translatable("menu.warp_pipes.warp_pipe.water_spout_button");
        this.waterSpoutButton = this.addRenderableWidget(new Button.Builder(waterSpout, (b) -> {
            this.waterSpoutButtonOnPress();
        }).bounds(x + 34, y + 18, 24, 24)
                .tooltip(Tooltip.create(Component.translatable("menu.warp_pipes.warp_pipe.water_spout_button.tooltip")))
                .createNarration(supplier -> Component.translatable("menu.warp_pipes.warp_pipe.water_spout_button.narrate")).build());
        this.waterSpoutButton.setAlpha(0);

        final Component height = Component.translatable("menu.warp_pipes.warp_pipe.water_spout_slider.height");
        waterSpoutSlider = this.addRenderableWidget(new WaterSpoutSlider(x + 61, y + 18, 108, 24,
                height, Component.literal(""), 0D, 16D, WarpPipeBlockEntity.spoutHeightStatic, 1D, 0, true));
        waterSpoutSlider.setTooltip(Tooltip.create(Component.translatable("menu.warp_pipes.warp_pipe.water_spout_slider.tooltip")));

        final Component bubbles = Component.translatable("menu.warp_pipes.warp_pipe.bubbles_button");
        this.bubblesButton = this.addRenderableWidget(new Button.Builder(bubbles, (b) -> {
            this.bubblesButtonOnPress();
        }).bounds(x + 34, y + 45, 24, 24)
                .tooltip(Tooltip.create(Component.translatable("menu.warp_pipes.warp_pipe.bubbles_button.tooltip")))
                .createNarration(supplier -> Component.translatable("menu.warp_pipes.warp_pipe.bubbles_button.narrate")).build());
        this.bubblesButton.setAlpha(0);

        final Component distance = Component.translatable("menu.warp_pipes.warp_pipe.bubbles_slider.height");
        bubblesSlider = this.addRenderableWidget(new BubblesSlider(x + 61, y + 45, 108, 24,
                distance, Component.literal(""), 0D, 16D, 3D, 1D, 0, true));
        bubblesSlider.setTooltip(Tooltip.create(Component.translatable("menu.warp_pipes.warp_pipe.bubbles_slider.tooltip")));
    }

    @Override
    // Draws the screen and all the components in it.
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    public void closeButtonOnPress() {
        Player player = this.inventory.player;
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null && !player.isCreative() && Config.CREATIVE_CLOSE_PIPES.get() && world.isClientSide())
            player.displayClientMessage(Component.translatable("display.warp_pipes.close_pipes.requires_creative").withStyle(ChatFormatting.RED), true);
        else PacketHandler.sendToServer(new SCloseStatePacket(WarpPipeBlockEntity.getPos(), Boolean.TRUE));
    }

    public void bubblesButtonOnPress() {
        Player player = this.inventory.player;
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null && !player.isCreative() && Config.CREATIVE_BUBBLES.get() && world.isClientSide())
            player.displayClientMessage(Component.translatable("display.warp_pipes.pipe_bubbles.requires_creative").withStyle(ChatFormatting.RED), true);
        else PacketHandler.sendToServer(new SPipeBubblesStatePacket(WarpPipeBlockEntity.getPos(), Boolean.TRUE));
    }

    public void bubblesSliderOnPress(double mouseX, double mouseY) {
        Player player = this.inventory.player;
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null && !player.isCreative() && Config.CREATIVE_BUBBLES.get() && world.isClientSide())
            player.displayClientMessage(Component.translatable("display.warp_pipes.pipe_bubbles.requires_creative").withStyle(ChatFormatting.RED), true);
    }

    public void waterSpoutButtonOnPress() {
        Player player = this.inventory.player;
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null && !player.isCreative() && Config.CREATIVE_WATER_SPOUT.get() && world.isClientSide())
            player.displayClientMessage(Component.translatable("display.warp_pipes.water_spouts.requires_creative").withStyle(ChatFormatting.RED), true);
        else PacketHandler.sendToServer(new SWaterSpoutStatePacket(WarpPipeBlockEntity.getPos(), Boolean.TRUE));
//        else if (this.minecraft != null) this.minecraft.getConnection().send(new SWaterSpoutStatePacket(WarpPipeBlockEntity.getPos(), Boolean.TRUE));
    }

    public void waterSpoutSliderOnPress(double mouseX, double mouseY) {
        Player player = this.inventory.player;
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null && !player.isCreative() && Config.CREATIVE_WATER_SPOUT.get() && world.isClientSide())
            player.displayClientMessage(Component.translatable("display.warp_pipes.water_spouts.requires_creative").withStyle(ChatFormatting.RED), true);
        else if (waterSpoutSlider.isMouseOver(mouseX, mouseY)) {
            int spoutHeight = waterSpoutSlider.getValueInt();
            this.spoutHeight = WarpPipeBlockEntity.spoutHeightStatic;
            BlockPos clickedPos = getClickedPos();
            PacketHandler.sendToServer(new SWaterSpoutSliderPacket(clickedPos, spoutHeight));
        }
    }

    public BlockPos getClickedPos() {
        HitResult hitResult = Minecraft.getInstance().hitResult;
        if (hitResult instanceof BlockHitResult) {
            return ((BlockHitResult) hitResult).getBlockPos();
        }
        return null;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (waterSpoutSlider.isMouseOver(mouseX, mouseY))
            waterSpoutSliderOnPress(mouseX, mouseY);
        if (bubblesSlider.isMouseOver(mouseX, mouseY))
            bubblesSliderOnPress(mouseX, mouseY);
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
