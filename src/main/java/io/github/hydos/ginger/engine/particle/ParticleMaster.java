package io.github.hydos.ginger.engine.particle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.github.hydos.ginger.engine.cameras.ThirdPersonCamera;
import io.github.hydos.ginger.engine.mathEngine.matrixes.Matrix4f;
import io.github.hydos.ginger.engine.renderEngine.renderers.ParticleRenderer;

public class ParticleMaster {
	
	private static Map<ParticleTexture,List<Particle>> particles = new HashMap<ParticleTexture, List<Particle>>();
	private static ParticleRenderer particleRenderer;
	
	public static void init(Matrix4f projectionMatrix) {
		particleRenderer = new ParticleRenderer(projectionMatrix);
		
	}
	
	public static void update(ThirdPersonCamera camera) {
		Iterator<Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
		while(mapIterator.hasNext()) {
			List<Particle> list = mapIterator.next().getValue();
			Iterator<Particle> iterator= list.iterator();
			while(iterator.hasNext()) {
				Particle p = iterator.next();
				boolean stillAlive = p.update(camera);
				if(!stillAlive) {
					iterator.remove();
					if(list.isEmpty()) {
						mapIterator.remove();
					}
				}
			}
			InsertionSort.sortHighToLow(list);
		}
	}
	
	public static void renderParticles(ThirdPersonCamera camera) {
		particleRenderer.render(particles, camera);
	}
	
	public static void cleanUp() {
		particleRenderer.cleanUp();
	}
	
	public static void addParticle(Particle particle) {
		List<Particle> list = particles.get(particle.getTexture());
		if(list==null) {
			list = new ArrayList<Particle>();
			particles.put(particle.getTexture(), list);
		}
		list.add(particle);
	}
	
	
	
	
}
