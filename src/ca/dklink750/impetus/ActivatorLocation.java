package ca.dklink750.impetus;

import org.bukkit.World;

// TODO: Refactor code to use getters rather than directly accessing members
public class ActivatorLocation {
    final public World world;
    final public Integer x;
    final public Integer y;
    final public Integer z;

    public ActivatorLocation(World world, Integer x, Integer y, Integer z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (getClass() != o.getClass()) {
            return false;
        }
        if (o == this) {
            return true;
        }
        boolean equals = true;
        ActivatorLocation activatorLocation = (ActivatorLocation) o;
        if (world.getUID() != activatorLocation.world.getUID() ||
                !x.equals(activatorLocation.x) ||
                !y.equals(activatorLocation.y) ||
                !z.equals(activatorLocation.z)) {
            equals = false;
        }
        return equals;
    }
}
