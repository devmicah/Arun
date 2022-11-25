package dev.micah.arun.generator;

import dev.micah.arun.utils.ConfigUtil;
import dev.micah.arun.utils.Cuboid2D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.Random;

public class ParkourGenerator {

    private Location point1, point2;

    public ParkourGenerator(Location point1, Location point2) {
        this.point1 = point1;
        this.point2 = point2;
    }


    /**
     * @deprecated System not needed anymore as its only 1 dimension.
     *
     * Visit #generateRegion
     *
     */
    @Deprecated
    public void generate(Material blockType) {
        if (!(point1.getY() == point2.getY())) {
            Bukkit.getLogger().severe("You cannot generate parkour with locations on different Y levels. Generator failed!");
            return;
        }
        BlockFace blockFace = getPointsDirection();
        Location currentLoc = point1.clone();
        double distance = currentLoc.distance(point2);
        double defaultXZ = blockFace == BlockFace.EAST || blockFace == BlockFace.WEST ? point1.getZ() : point1.getX();
        while (distance > 3.0) {
            Location setBlockLoc = nextBlockFromFace(currentLoc, blockFace);
            setBlockLoc.getBlock().setType(Material.STONE);

            Location defaultLoc = setBlockLoc.clone();
            if (blockFace == BlockFace.EAST || blockFace == BlockFace.WEST) defaultLoc.setZ(defaultXZ); else defaultLoc.setX(defaultXZ);
            distance = defaultLoc.distance(point2);
            currentLoc = setBlockLoc.clone();

            if (distance > 50) {
                break;
            }
        }
    }

    public void generateRegion(List<Material> materials) {

        /**
         *
         * Understanding the Generation System in Regions (3D bases)
         *
         * Laws of Generation:
         * - Must ALWAYS have 2 different points left or right of the region
         * - Must ALWAYS have a "heavy" side or a side which it goes off to.
         *   For example if the Z increases in a region that means the rest
         *   of the regions will be off to the right.
         * - The "heavy" side will vary greatly depending on the block face
         *   and weather the X/Z is greater in point 1 or point 2
         *
         * Cheat Sheet:
         *
         * To understand the cheat sheet read the following:
         *   The info is separated by a '/'. The left side is the
         *   block face and next to it is the value that changes
         *   to determine the heavy side. On the right of the '/'
         *   is what what terms need to happen in order for that
         *   to be the heavy side. P2X means Point 2 X, if its greater
         *   then P1X (..or Point 1 X) then its the right side.
         *
         * - North (X) / P2X > P1X = Right Side (+)
         * - North (X) / P2X < P1X = Left Side (-)
         * - South (X) / P2X > P1X = Left Side (+)
         * - South (X) / P2X < P1X = Right Side (-)
         * - East (Z) / P2X > P1X = Left Side (+)
         * - East (Z) / P2X < P1X = Right Side (-)
         * - West (Z) / P1Z > P2Z = Left Side (-)
         * - West (Z) / P1Z < P2Z = Right Side (+)
         *
         **/

        Cuboid2D region = new Cuboid2D(point1, point2);

        if (region.getLoc1().getY() == region.getLoc2().getY()) {
            region.getBlocksFromRegion().forEach(block -> {
                int[] offsets = new int[]{-1, 0, 1};
                for (int offset : offsets) {
                    block.getLocation().add(0, offset, 0).getBlock().setType(Material.AIR);
                }
                Random random = new Random();
                int chanceOutOf100 = 100 - (int) ConfigUtil.getConfigValue("generation-percent");
                boolean isParkour = random.nextInt(100) > chanceOutOf100; // 14% chance for a block to generate
                Material randomBlockType = materials.get(new Random().nextInt(materials.size()));
                if (isParkour) {
                    boolean isYChanged = random.nextBoolean();
                    boolean negative = random.nextBoolean();
                    if (block.getType() == Material.AIR) {
                        if (isYChanged) {
                            block.getLocation().add(0, negative ? -1 : 1, 0).getBlock().setType(randomBlockType);
                            return;
                        }
                        block.setType(randomBlockType);
                    }
                }
            });
        } else {
            Bukkit.getLogger().severe("Invalid region positions! Y level higher then other position.");
        }
    }

    private Location nextBlockFromFace(Location startLoc, BlockFace blockFace) {
        Random random = new Random(); int leftRightOffset = random.nextInt(2); int xzOffset = random.nextInt(4) + 2; int yOffset = random.nextInt(1);
        boolean negative = random.nextBoolean();
        if (yOffset > 0 && negative) {
            xzOffset -= 1;
            leftRightOffset -= 1;
        }
        switch (blockFace) {
            case WEST:
                return startLoc.clone().add(-xzOffset, negative ? -yOffset : yOffset, negative ? -leftRightOffset : leftRightOffset);
            case EAST:
                return startLoc.clone().add(xzOffset, negative ? -yOffset : yOffset, negative ? -leftRightOffset : leftRightOffset);
            case NORTH:
                return startLoc.clone().add(negative ? -leftRightOffset : leftRightOffset, negative ? -yOffset : yOffset, -xzOffset);
            case SOUTH:
                return startLoc.clone().add(negative ? -leftRightOffset : leftRightOffset, negative ? -yOffset : yOffset, xzOffset);
        }
        return null;
    }

    private BlockFace getPointsDirection() {
        double p1X = point1.getX();
        double p1Z = point1.getZ();

        double p2X = point2.getX();
        double p2Z = point2.getZ();

        if (p1X < p2X) return BlockFace.EAST; // Add to X, Add/Subtract from Z for left/right
        if (p1X > p2X) return BlockFace.WEST; // Subtract from X, Add/Subtract from Z for left/right
        if (p1Z < p2Z) return BlockFace.SOUTH; // Add to Z, Add/Subtract from X for left/right
        if (p1Z > p2Z) return BlockFace.NORTH; // Subtract from Z, Add/Subtract from X for left/right
        return null;
    }

}
