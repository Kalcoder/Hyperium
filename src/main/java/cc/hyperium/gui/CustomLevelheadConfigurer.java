package cc.hyperium.gui;

import cc.hyperium.mods.sk1ercommon.Multithreading;
import cc.hyperium.mods.sk1ercommon.ResolutionUtil;
import cc.hyperium.netty.NettyClient;
import cc.hyperium.netty.packet.packets.serverbound.ServerCrossDataPacket;
import cc.hyperium.purchases.PurchaseApi;
import cc.hyperium.utils.ChatColor;
import cc.hyperium.utils.JsonHolder;
import cc.hyperium.utils.UUIDUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * Created by mitchellkatz on 5/2/18. Designed for production use on Sk1er.club
 */
public class CustomLevelheadConfigurer extends HyperiumGui {
    private GuiTextField header;
    private GuiTextField level;

    private JsonHolder levelhead_propose = new JsonHolder();

    @Override
    public void initGui() {
        super.initGui();
        int i = ResolutionUtil.current().getScaledWidth() - 20;
        header = new GuiTextField(nextId(), fontRendererObj, 5, 30, i / 2, 20);
        level = new GuiTextField(nextId(), fontRendererObj, ResolutionUtil.current().getScaledWidth() / 2 + 5, 30, i / 20, 20);
        Multithreading.runAsync(() -> {
            JsonHolder jsonHolder = PurchaseApi.getInstance().get("https://sk1er.club/newlevel/" + UUIDUtil.getUUIDWithoutDashes());
            header.setText(jsonHolder.optString("text"));
            level.setText(jsonHolder.optString("true_footer"));
        });
        Multithreading.runAsync(() -> {
            levelhead_propose = PurchaseApi.getInstance().get("https://api.hyperium.cc/levelhead_propose//" + UUIDUtil.getUUIDWithoutDashes());

        });

    }

    @Override
    protected void pack() {
        int i = ResolutionUtil.current().getScaledWidth() - 20;

        reg("RESET", new GuiButton(nextId(), 5, 55, i / 2, 20, "Reset to default"), button -> {
            NettyClient.getClient().write(ServerCrossDataPacket.build(new JsonHolder().put("levelhead_propose", true).put("reset", true)));
        }, button -> {

        });
        reg("PROPOSE", new GuiButton(nextId(), ResolutionUtil.current().getScaledWidth() / 2 + 5, 55, i / 2, 20, "Reset to default"), button -> {
            NettyClient.getClient().write(ServerCrossDataPacket.build(new JsonHolder().put("levelhead_propose", true).put("propose", true).put("header", header.getText()).put("level", level.getText())));
        }, button -> {

        });
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        header.drawTextBox();
        level.drawTextBox();
        int stringWidth = fontRendererObj.getStringWidth("Custom Levelhead Configurer");
        drawCenteredString(mc.fontRendererObj, ChatColor.YELLOW + "Custom Levelhead Configurer", this.width / 2, 10, Color.WHITE.getRGB());
        drawHorizontalLine(this.width / 2 - stringWidth / 2 - 5, this.width / 2 + stringWidth / 2 + 5, 20, Color.WHITE.getRGB());

        if (levelhead_propose.optBoolean("denied")) {
            drawCenteredString(fontRendererObj, ChatColor.WHITE + "Status: " + ChatColor.RED + "Denied", width / 2, 80, Color.WHITE.getRGB());
            return;
        }
        if (levelhead_propose.optBoolean("enabled")) {
            int i = 90;
            drawCenteredString(fontRendererObj, ChatColor.WHITE + "Status: " + ChatColor.GREEN + "Accepted", width / 2, 80, Color.WHITE.getRGB());

            List<String> header = fontRendererObj.listFormattedStringToWidth(ChatColor.YELLOW + "Header: " + ChatColor.AQUA + levelhead_propose.optString("header"), width - 20);
            for (String s : header) {
                drawCenteredString(fontRendererObj, s, width / 2, i, Color.WHITE.getRGB());
                i += 10;
            }
            header = fontRendererObj.listFormattedStringToWidth(ChatColor.YELLOW + "Level: " + ChatColor.AQUA + levelhead_propose.optString("true_footer"), width - 20);
            for (String s : header) {
                drawCenteredString(fontRendererObj, s, width / 2, i, Color.WHITE.getRGB());
                i += 10;
            }
        } else {
            int i = 90;

            drawCenteredString(fontRendererObj, ChatColor.WHITE + "Status: " + ChatColor.YELLOW + "Pending", width / 2, 80, Color.WHITE.getRGB());
            List<String> header = fontRendererObj.listFormattedStringToWidth(ChatColor.YELLOW + "Header: " + ChatColor.AQUA + levelhead_propose.optString("header"), width - 20);
            for (String s : header) {
                drawCenteredString(fontRendererObj, s, width / 2, i, Color.WHITE.getRGB());
                i += 10;
            }
            header = fontRendererObj.listFormattedStringToWidth(ChatColor.YELLOW + "Level: " + ChatColor.AQUA + levelhead_propose.optString("strlevel"), width - 20);
            for (String s : header) {
                drawCenteredString(fontRendererObj, s, width / 2, i, Color.WHITE.getRGB());
                i += 10;
            }
        }

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        header.mouseClicked(mouseX, mouseY, mouseButton);
        level.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        header.textboxKeyTyped(typedChar, keyCode);
        level.textboxKeyTyped(typedChar, keyCode);
    }
}
