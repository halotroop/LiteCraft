package io.github.hydos.ginger.engine.obj.normals;

import java.util.ArrayList;
import java.util.List;

import io.github.hydos.ginger.engine.mathEngine.vectors.Vector3f;

public class VertexNM {
	
	private static final int NO_INDEX = -1;
	
	private Vector3f position;
	private int textureIndex = NO_INDEX;
	private int normalIndex = NO_INDEX;
	private VertexNM duplicateVertex = null;
	private int index;
	private float length;
	private List<Vector3f> tangents = new ArrayList<Vector3f>();
	private Vector3f averagedTangent = new Vector3f(0, 0, 0);
	
	protected VertexNM(int index, Vector3f position){
		this.index = index;
		this.position = position;
		this.length = position.length();
	}
	
	protected void addTangent(Vector3f tangent){
		tangents.add(tangent);
	}
	
	//NEW
	protected VertexNM duplicate(int newIndex){
		VertexNM vertex = new VertexNM(newIndex, position);
		vertex.tangents = this.tangents;
		return vertex;
	}
	
	protected void averageTangents(){
		if(tangents.isEmpty()){
			return;
		}
		for(Vector3f tangent : tangents){
			Vector3f.add(averagedTangent, tangent, averagedTangent);
		}
		averagedTangent.normalise();
	}
	
	protected Vector3f getAverageTangent(){
		return averagedTangent;
	}
	
	protected int getIndex(){
		return index;
	}
	
	protected float getLength(){
		return length;
	}
	
	protected boolean isSet(){
		return textureIndex!=NO_INDEX && normalIndex!=NO_INDEX;
	}
	
	protected boolean hasSameTextureAndNormal(int textureIndexOther,int normalIndexOther){
		return textureIndexOther==textureIndex && normalIndexOther==normalIndex;
	}
	
	protected void setTextureIndex(int textureIndex){
		this.textureIndex = textureIndex;
	}
	
	protected void setNormalIndex(int normalIndex){
		this.normalIndex = normalIndex;
	}

	protected Vector3f getPosition() {
		return position;
	}

	protected int getTextureIndex() {
		return textureIndex;
	}

	protected int getNormalIndex() {
		return normalIndex;
	}

	protected VertexNM getDuplicateVertex() {
		return duplicateVertex;
	}

	protected void setDuplicateVertex(VertexNM duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}

}
