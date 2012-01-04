package hobo;

// this is used for card color as well as player color, which are
// two very different concepts, so pay attention.
public enum Color {
	// NOTE: grey is used for wildcards
	BLACK(119, 119, 119),
	WHITE(255, 255, 255),
	YELLOW(255, 245, 23),
	GREEN(141, 203, 141),
	RED(227, 115, 115),
	BLUE(121, 124, 222),
	ORANGE(236, 191, 108),
	PURPLE(191, 152, 193),
	GREY(223, 223, 223);
	
	public static final Color[] all = values();

	public final java.awt.Color awtColor;

	private Color(int r, int g, int b) {
		awtColor = new java.awt.Color(r, g, b);
	}
	
	public static Color designated_by(String name) {
		return valueOf(name.toUpperCase());
	}
}
