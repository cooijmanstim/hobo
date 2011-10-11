package hobo;

public enum Color {
	BLACK, GREY, WHITE, YELLOW, GREEN, RED, BLUE, BROWN, PINK;

	public static Color designated_by(String name) {
		return valueOf(name.toUpperCase());
	}
}
