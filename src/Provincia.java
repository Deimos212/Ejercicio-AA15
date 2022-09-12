
public class Provincia {
	
	private String provincia;
	
	public Provincia(String provincia) {
		this.provincia = provincia;
	}
	
	// Getters & Setters
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	@Override
	public String toString() {
		return "Provincia [provincia=" + provincia + "]";
	}

	
}
