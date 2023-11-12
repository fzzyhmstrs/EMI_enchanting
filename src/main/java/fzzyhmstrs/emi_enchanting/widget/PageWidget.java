package fzzyhmstrs.emi_enchanting.widget;

import com.google.common.collect.Lists;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;

import java.util.List;

public class PageWidget extends Widget implements WidgetHolder {
    private final List<Widget> widgets = Lists.newArrayList();
    private final int width, height;
    private final Bounds bounds;
    private boolean isActive = false;

    public PageWidget(int x, int y, int width, int height){
        this.width = width;
        this.height = height;
        this.bounds = new Bounds(x, y, width, height);
    }

    public void setActive(boolean active){
        this.isActive = active;
    }

    @Override
    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public void render(DrawContext draw, int mouseX, int mouseY, float delta) {
        if (!isActive) return;
        for (Widget widget : widgets){
            widget.render(draw, mouseX, mouseY, delta);
        }
    }

    @Override
    public List<TooltipComponent> getTooltip(int mouseX, int mouseY){
        if (!isActive) return List.of();
        for (Widget widget : widgets){
            if (widget.getBounds().contains(mouseX, mouseY)) return widget.getTooltip(mouseX, mouseY);
        }
        return List.of();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!isActive) return false;
        for (Widget widget : widgets){
            if (widget.getBounds().contains(mouseX, mouseY)) {
                if (widget.mouseClicked(mouseX, mouseY, button)) return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public <T extends Widget> T add(T widget) {
        widgets.add(widget);
        return widget;
    }
}
