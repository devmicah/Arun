package dev.micah.arun.api.region;

import dev.micah.arun.generator.GeneratorIO;
import dev.micah.arun.utils.Cuboid2D;
import dev.micah.arun.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class RegionAPI {

    private YamlConfiguration config;
    private GeneratorIO generatorIO;

    public RegionAPI(GeneratorIO generatorIO) {
        this.generatorIO = generatorIO;
        this.config = generatorIO.getConfig();
    }

    public void addRegion(String gameName, Location point1, Location point2) {
        List<String> regions = config.getStringList("regions." + gameName + ".points");
        regions.add(LocationUtil.asString(point1) + "%" + LocationUtil.asString(point2));
        config.set("regions." + gameName + ".points", regions);

        generatorIO.save();

        List<String> blockTypes = config.getStringList("regions." + gameName + ".types");
        blockTypes.add(Material.STONE.name());
        config.set("regions." + gameName + ".types", blockTypes);

        generatorIO.save();
    }

    public boolean containsRegion(String gameName, Location point1, Location point2) {
        for (Cuboid2D region : getRegions(gameName)) {
            if (region.getLoc1().toString().equals(point1.toString()) && region.getLoc2().toString().equals(point2.toString())) {
                return true;
            }
        }
        return false;
    }

    public void clearBlockTypes(String gameName) {
        config.set("regions." + gameName + ".types", null);
        generatorIO.save();
    }

    public void removeRegion(String gameName, int i) {
        List<String> regions = config.getStringList("regions." + gameName + ".points");
        regions.remove(i);
        config.set("regions." + gameName + ".points", regions);
        generatorIO.save();
    }

    public List<Cuboid2D> getRegions(String gameName) {
        List<String> regions = config.getStringList("regions." + gameName + ".points");
        List<Cuboid2D> cuboid2DList = new ArrayList<>();
        for (String s : regions) {
            String[] values = s.split("%");
            cuboid2DList.add(new Cuboid2D(LocationUtil.fromString(values[0]), LocationUtil.fromString(values[1])));
        }
        return cuboid2DList;
    }

    public void addBlockTypes(String gameName, Material... materials) {
        List<String> blockTypes = config.getStringList("regions." + gameName + ".types");
        for (Material m : materials) {
            if (blockTypes.contains(m.name())) continue;
            blockTypes.add(m.name());
        }
        config.set("regions." + gameName + ".types", blockTypes);
        generatorIO.save();
    }

    public List<Material> getBlockTypes(String gameName) {
        List<Material> materialList = new ArrayList<>();
        List<String> blockTypes = config.getStringList("regions." + gameName + ".types");
        for (String s : blockTypes) {
            materialList.add(Material.valueOf(s));
        }
        return materialList;
    }

}
