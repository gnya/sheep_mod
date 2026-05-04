package io.github.gnya.sheep_mod;

import com.mojang.logging.LogUtils;
import io.github.gnya.sheep_mod.particles.SleepParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(SheepMod.MODID)
public class SheepMod {
  public static final String MODID = "sheep_mod";
  public static final Logger LOGGER = LogUtils.getLogger();

  public static final DeferredRegister<ParticleType<?>> PARTICLES =
      DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);

  public static final RegistryObject<SimpleParticleType> SLEEP_PARTICLE =
      PARTICLES.register("sleep", () -> new SimpleParticleType(false));

  public SheepMod(FMLJavaModLoadingContext context) {
    PARTICLES.register(context.getModBusGroup());

    RegisterParticleProvidersEvent.BUS.addListener(
        event -> event.registerSpriteSet(SLEEP_PARTICLE.get(), SleepParticle.Provider::new));
  }
}
