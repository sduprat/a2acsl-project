package org.eclipse.uml.a2acsl.values;

/**
 * Represents the value of a structural feature access Examples : obj.id,
 * obj.tab -> at(i)
 * 
 * @author A560169
 * 
 */
public class FeatureValue implements Value {

	private String feature;
	private Value source;
	private String index;
	private boolean staticAccess;

	/**
	 * Creates a new FeatureValue
	 * 
	 * @param feature
	 * @param source
	 * @param index
	 * @param staticAccess
	 */
	public FeatureValue(String feature, Value source, String index,
			boolean staticAccess) {
		this.feature = feature;
		this.source = source;
		this.index = index;
		this.staticAccess = staticAccess;
	}

	/**
	 * Returns true of the access is to an external feature
	 * 
	 * @return
	 */
	public boolean isStaticAccess() {
		return staticAccess;
	}

	/**
	 * Returns the name of the feature
	 * 
	 * @return
	 */
	public String getFeature() {
		return feature;
	}

	/**
	 * Return the value of the source or null
	 * 
	 * @return
	 */
	public Value getSource() {
		return source;
	}

	/**
	 * Returns the corresponding index if the access is to an item of a
	 * collection
	 * 
	 * @return
	 */
	public String getIndex() {
		return index;
	}

}
