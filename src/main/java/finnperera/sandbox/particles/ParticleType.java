package finnperera.sandbox.particles;

import finnperera.sandbox.particles.placeable.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum ParticleType {
    STEAM(Steam.class),
    SMOKE(Smoke.class),
    SAND(Sand.class),
    GLASS(Glass.class),
    WATER(Water.class),
    OIL(Oil.class),
    LAVA(Lava.class),
    ROCK(Rock.class),
    WOOD(Wood.class),
    FIRE(Fire.class),
    EMPTY(Empty.class);

    private final Class<? extends Particle> particleTypeClass;

    ParticleType(Class<? extends Particle> particleTypeClass) {
        this.particleTypeClass = particleTypeClass;
    }

    public Class<? extends Particle> getParticleTypeClass() {
        return particleTypeClass;
    }

    // Not a good way of doing things but it does work
    public Particle createInstance(int x, int y) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Constructor<? extends Particle> constructor = particleTypeClass.getConstructor(int.class, int.class);
        return constructor.newInstance(x, y);
    }
}
