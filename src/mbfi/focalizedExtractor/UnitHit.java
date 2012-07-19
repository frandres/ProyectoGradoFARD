package mbfi.focalizedExtractor;

public class UnitHit implements Comparable<UnitHit>{

	private double hitMeasure;
	private String unit;
	
	
	public double getHitMeasure() {
		return hitMeasure;
	}


	public String getUnit() {
		return unit;
	}

	public UnitHit(double hitMeasure, String unit) {
		super();
		this.hitMeasure = hitMeasure;
		this.unit = unit;
	}


	@Override
	public int compareTo(UnitHit o) {

		if (getHitMeasure() == o.getHitMeasure()){
			return 0;
		}
		
		if (getHitMeasure() > o.getHitMeasure()){
			return -1;
		}
		
		if (getHitMeasure() < o.getHitMeasure()){
			return 1;
		}
		
		return 0;
	}
	
	
	
}
