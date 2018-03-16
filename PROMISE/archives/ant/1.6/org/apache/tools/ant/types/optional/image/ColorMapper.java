package org.apache.tools.ant.types.optional.image;

import java.awt.Color;

/**
 *
 * @see org.apache.tools.ant.taskdefs.optional.image.Image
 */
public final class ColorMapper {
    public static final String COLOR_BLACK = "black";
    public static final String COLOR_BLUE = "blue";
    public static final String COLOR_CYAN = "cyan";
    public static final String COLOR_DARKGRAY = "darkgray";
    public static final String COLOR_GRAY = "gray";
    public static final String COLOR_LIGHTGRAY = "lightgray";
    public static final String COLOR_DARKGREY = "darkgrey";
    public static final String COLOR_GREY = "grey";
    public static final String COLOR_LIGHTGREY = "lightgrey";
    public static final String COLOR_GREEN = "green";
    public static final String COLOR_MAGENTA = "magenta";
    public static final String COLOR_ORANGE = "orange";
    public static final String COLOR_PINK = "pink";
    public static final String COLOR_RED = "red";
    public static final String COLOR_WHITE = "white";
    public static final String COLOR_YELLOW = "yellow";

    /**
     * @todo refactor to use an EnumeratedAttribute (maybe?)
     */
    public static final Color getColorByName(String color_name) {
        color_name = color_name.toLowerCase();

        if (color_name.equals(COLOR_BLACK)) {
            return Color.black;
        } else if (color_name.equals(COLOR_BLUE)) {
            return Color.blue;
        } else if (color_name.equals(COLOR_CYAN)) {
            return Color.cyan;
        } else if (color_name.equals(COLOR_DARKGRAY) || color_name.equals(COLOR_DARKGREY)) {
            return Color.darkGray;
        } else if (color_name.equals(COLOR_GRAY) || color_name.equals(COLOR_GREY)) {
            return Color.gray;
        } else if (color_name.equals(COLOR_LIGHTGRAY) || color_name.equals(COLOR_LIGHTGREY)) {
            return Color.lightGray;
        } else if (color_name.equals(COLOR_GREEN)) {
            return Color.green;
        } else if (color_name.equals(COLOR_MAGENTA)) {
            return Color.magenta;
        } else if (color_name.equals(COLOR_ORANGE)) {
            return Color.orange;
        } else if (color_name.equals(COLOR_PINK)) {
            return Color.pink;
        } else if (color_name.equals(COLOR_RED)) {
            return Color.red;
        } else if (color_name.equals(COLOR_WHITE)) {
            return Color.white;
        } else if (color_name.equals(COLOR_YELLOW)) {
            return Color.yellow;
        }
        return Color.black;
    }

}
