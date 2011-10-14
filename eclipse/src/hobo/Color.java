package hobo;

// this is used for card color as well as player color, which are
// two very different concepts, so pay attention.
public enum Color {
	BLACK, GREY, WHITE, YELLOW, GREEN, RED, BLUE, BROWN, PINK;

	private final java.awt.Color awtColor;

	private Color() {
		java.awt.Color c = null;
		try {
			c = (java.awt.Color)Class.forName("java.awt.Color").getField(this.name()).get(null);
		} catch (Throwable t) {}
		awtColor = c;
	}
	
	public static Color designated_by(String name) {
		return valueOf(name.toUpperCase());
	}
	
	public java.awt.Color toAWTColor() {
		return awtColor;
	}
}
