package hlt;

public class Entity extends Position {

    private final int owner;
    private final int id;
    private final int health;
    private final double radius;

    public Entity(final int owner, final int id, final double xPos, final double yPos, final int health, final double radius) {
        super(xPos, yPos);
        this.owner = owner;
        this.id = id;
        this.health = health;
        this.radius = radius;
    }

    public int getOwner() {
        return owner;
    }

    public int getId() {
        return id;
    }

    public int getHealth() {
        return health;
    }

    public double getRadius() {
        return radius;
    }

    public double getDistance(Entity other) {
        double dx = this.getXPos() - other.getXPos();
        double dy = this.getYPos() - other.getYPos();
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    @Override
    public String toString() {
        return "Entity[" +
                super.toString() +
                ", owner=" + owner +
                ", id=" + id +
                ", health=" + health +
                ", radius=" + radius +
                "]";
    }
}
