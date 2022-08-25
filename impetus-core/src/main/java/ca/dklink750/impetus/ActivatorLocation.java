package ca.dklink750.impetus;

import org.bukkit.World;
import java.util.Objects;

public class ActivatorLocation {
    final private World world;
    final private Integer x;
    final private Integer y;
    final private Integer z;

    public ActivatorLocation(World world, Integer x, Integer y, Integer z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivatorLocation that = (ActivatorLocation) o;
        return Objects.equals(world, that.world) && Objects.equals(x, that.x) && Objects.equals(y, that.y) && Objects.equals(z, that.z);
    }

    public World world() {
        return world;
    }

    public Integer x() {
        return x;
    }

    public Integer y() {
        return y;
    }

    public Integer z() {
        return z;
    }
}
