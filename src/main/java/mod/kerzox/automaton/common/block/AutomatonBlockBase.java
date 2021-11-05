package mod.kerzox.automaton.common.block;

import mod.kerzox.automaton.Automaton;
import mod.kerzox.automaton.registry.AutomatonRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;


public class AutomatonBlockBase extends Block {

    public AutomatonBlockBase(String registryName, boolean asItem, Properties properties) {
        super(properties);
        RegistryObject<Block> ret = AutomatonRegistry.BLOCKS.register(registryName, () -> this);
        AutomatonRegistry.ITEMS.register(registryName, () -> new BlockItem(ret.get(), new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
    }

}
