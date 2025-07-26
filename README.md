![The Volatile Addon](https://github.com/DarkFoxYT/SPV_addon/blob/a06a60760aee728074f5151bae3a80699fcb62a9/src/main/resources/Volitileicon.png)

# The Volatile Addon for Space Potatoes Found Footage Mod

**Version:** 1.0.2 - BETA 4  
**Minecraft Version:** 1.20.1  
**Mod Loader:** Fabric  

SPV Addon is a comprehensive content expansion for [Space Potatoes Backrooms Mod](https://modrinth.com/mod/minecraft-found-footage) that significantly enhances the horror and immersion of the Backrooms experience. This addon introduces survival mechanics, new levels, entities, and atmospheric features designed for both casual players and hardcore survival enthusiasts.

---

## 🎮 Core Survival Systems

### 🧠 **Sanity System**
- **Mental Health Tracking**: Monitor your character's psychological state as you explore the endless halls
- **Environmental Effects**: Darkness, isolation, and entity encounters drain sanity
- **Progressive Symptoms**: Low sanity causes hallucinations, screen distortions, and dangerous effects
- **Light Sources**: Stay near sanity lights to maintain mental stability
- **Restoration Items**: Use almond water and other items to restore sanity

### 💧 **Thirst System**
- **Hydration Management**: Track your water levels with a dedicated thirst meter
- **Multiple Water Sources**: 
  - **Water Bottle**: Basic hydration (+25 thirst)
  - **Dirty Water**: Risky hydration (+10 thirst, may cause negative effects)
  - **Purified Water**: Premium hydration (+40 thirst)
  - **Energy Drink**: High hydration with temporary speed boost (+35 thirst)
- **Dehydration Effects**: Progressive weakness, slowness, and eventual damage
- **Environmental Factors**: Hot levels increase thirst drain rate

### 🔋 **Battery System**
- **Realistic Power Management**: Flashlights require batteries that drain over time
- **Battery Health**: Batteries degrade with use, reducing maximum capacity
- **Rechargeable Batteries**: Find battery items to recharge your flashlight
- **Environmental Drain**: Different levels affect battery consumption rates
- **Strategic Resource Management**: Plan your exploration around battery life

### 🐍 **Crawling Mechanic**
- **Enhanced Movement**: Crawl through vents, tight spaces, and low areas
- **Stealth Gameplay**: Avoid entities by crawling through hidden passages
- **Configurable Controls**: Customizable crawling keybinds and settings
- **Realistic Physics**: Proper collision detection and movement mechanics

---

## 🌍 New Backrooms Levels

### 🏃 **Level RUN**
- **Survival Challenge**: Escape by traveling exactly 200 blocks
- **Damage Over Time**: Constant health drain (0.5 hearts every 3 seconds)
- **Distance Tracking**: Real-time progress monitoring with action bar updates
- **Automatic Transition**: Teleports to random Backrooms level after 200 blocks
- **Progress Commands**: Check your progress with `/spv run progress`
- **Smiler Spawning**: Dangerous entities spawn periodically to increase pressure

### 🐱 **Level Kitty**
- **Feline Paradise**: Cat-themed level with unique mechanics
- **Kitty Entity**: Friendly but mysterious cat companion
- **Interactive Elements**: Cat-themed blocks and lighting systems
- **Safe Haven**: Relatively peaceful level for recovery


---

## 👹 Custom Entities

### 🔔 **Bell Walker**
- **Unique Design**: Six-legged creature with distinctive bell sounds
- **Tracking Behavior**: Follows players through levels
- **Audio Cues**: Bell sounds indicate proximity and danger level
- **Moderate Threat**: Balanced enemy for mid-level encounters

### 🐱 **Kitty**
- **Friendly Companion**: Non-hostile entity in Level Kitty
- **Interactive Behavior**: Responds to player presence and actions
- **Protective Mechanics**: May help players in certain situations
- **Unique AI**: Advanced behavior patterns and animations

---

## 🎵 Audio & Atmosphere

### 📼 **Lore System**
- **Tape Recordings**: Collectible audio logs scattered throughout levels
- **Story Elements**: Discover the history and secrets of the Backrooms
- **Character Development**: Learn about previous explorers and their fates
- **World Building**: Rich narrative that expands the Backrooms universe

---

## 🎮 Gameplay Features

### 🎯 **Custom Death Screen**
- **Glitch Effects**: Screen distortion and static overlay
- **Atmospheric Design**: Immersive death experience
- **Scanlines**: CRT monitor-style visual effects
- **Horror Aesthetics**: Maintains the found footage atmosphere

### 🎮 **Enhanced HUD**
- **System Meters**: Visual indicators for sanity, thirst, and battery
- **Fade Animations**: Smooth transitions and visual feedback
- **Color Coding**: Intuitive color system for status indication
- **Minimal Design**: Clean interface that doesn't break immersion

### 🎛️ **Server Compatibility**
- **Gamerule Integration**: Server administrators can control all systems
- **Multiplayer Optimized**: Designed for both singleplayer and multiplayer
- **Performance Focused**: Optimized code for smooth server operation
- **Configurable Systems**: Extensive customization options

---

## ⚙️ Server Administration

### 🎮 **Gamerules**
- `spvBatterySystem` - Enable/disable battery system
- `spvBatteryDrainRate` - Control battery drain speed (1-1000%)
- `spvThirstSystem` - Enable/disable thirst system
- `spvThirstDrainRate` - Control thirst drain speed (1-1000%)
- `spvThirstDamage` - Enable/disable thirst damage
- `spvSanitySystem` - Enable/disable sanity system
- `spvSanityDrainRate` - Control sanity drain speed (1-1000%)
- `spvSanityEffects` - Enable/disable sanity effects

### 💻 **Commands**
- `/spv battery <0-100>` - Set player battery level
- `/spv thirst <0-100>` - Set player thirst level
- `/spv sanity <0-100>` - Set player sanity level
- `/spv run progress` - Check Level RUN progress and distance traveled

---

## 📋 Requirements

### 🔧 **Required Dependencies**
- **[Space Potatoes Backrooms Mod](https://modrinth.com/mod/minecraft-found-footage)** - Base mod
- **[Fabric API](https://modrinth.com/mod/fabric-api)** - Fabric mod loader API
- **[GeckoLib](https://modrinth.com/mod/geckolib)** - Animation library
- **[Cardinal Components API](https://modrinth.com/mod/cardinal-components-api)** - Data components
- **[MidnightLib](https://modrinth.com/mod/midnightlib)** - Configuration library

### 🎤 **Optional Dependencies**
- **[Simple Voice Chat](https://modrinth.com/mod/simple-voice-chat)** - Enhanced voice communication
- **[Mod Menu](https://modrinth.com/mod/modmenu)** - In-game configuration menu

---

## 📦 Installation

1. **Download Dependencies**: Install all required mods listed above
2. **Download SPV Addon**: Get the latest release from the releases page
3. **Install Mods**: Place all `.jar` files in your `mods/` folder
4. **Launch Game**: Start Minecraft with Fabric loader
5. **Configure Settings**: Adjust settings via Mod Menu (optional)

---

## 🔧 Configuration

### 🎮 **Client Settings**
- **HUD Customization**: Adjust meter positions and colors
- **Audio Settings**: Control volume levels and sound effects
- **Visual Effects**: Toggle screen effects and animations
- **Control Bindings**: Customize crawling and interaction keys

### 🖥️ **Server Settings**
- **System Control**: Enable/disable individual survival systems
- **Difficulty Scaling**: Adjust drain rates and damage values
- **Performance Options**: Optimize for server hardware
- **Player Permissions**: Control command access levels

---

## 🐛 Known Issues & Compatibility

### ⚠️ **Current Limitations**
- Some features may not work in certain modded dimensions
- Performance may vary on lower-end hardware
- Voice chat integration requires Simple Voice Chat mod

### 🔄 **Compatibility**
- **Fabric 1.20.1**: Fully supported
- **Multiplayer**: Optimized for server play
- **Other Mods**: Generally compatible with most Fabric mods (veil might cause the issues not the volatile addon)

---

## 🤝 Contributing & Support

### 📞 **Support**
- **Issues**: Report bugs on the GitHub issues page
- **Documentation**: Check the wiki for detailed guides

### 💡 **Contributing**
- **Bug Reports**: Help us identify and fix issues
- **Feature Requests**: Suggest new content and improvements
- **Code Contributions**: Submit pull requests for review

---

## 📜 License & Credits

**Created by:** DarkFox Studios  
**License:** MIT License  
**Special Thanks:** Space Potato for the amazing base mod and to Herr Chaos for the refactor and help!

---

## 🎯 Items & Tools

### 🔋 **Survival Items**
- **Battery Item**: Rechargeable power source for flashlights (100% charge, stackable x2)
- **Canteen**: Restores sanity and thirst (+10 sanity, +50 thirst)
- **Almond Water**: Premium sanity restoration (+25 sanity, +10 thirst)
- **Dirty Almond Water**: Risky sanity boost (+2 sanity, +3 thirst, may cause negative effects)

### 💧 **Hydration Items**
- **Water Bottle**: Clean drinking water (+25 thirst)
- **Dirty Water**: Contaminated water source (+10 thirst, risk of negative effects)
- **Purified Water**: High-quality hydration (+40 thirst)
- **Energy Drink**: Hydration with speed boost (+35 thirst, temporary speed effect)

### 📼 **Lore Items**
- **Tape 1**: Audio recording with backstory and atmosphere
- **Additional Tapes**: More recordings planned for future updates

---

## 🎨 Visual & Audio Features

### 🖥️ **Custom HUD Elements**
- **Unified HUD System**: All survival meters in one clean interface
- **Fade Animations**: Smooth transitions and visual feedback
- **Color-Coded Status**: Intuitive color system (green=good, yellow=warning, red=danger)
- **Preset Color Options**: Choose from magenta, cyan, and other color schemes
- **Minimal Design**: Non-intrusive interface that maintains immersion

### 🎬 **Death Screen Enhancement**
- **Glitch Effects**: Screen distortion and digital artifacts
- **Static Overlay**: TV static effect for found footage atmosphere
- **Scanlines**: CRT monitor-style visual effects
- **Atmospheric Design**: Maintains horror theme even in death

### 🔊 **Audio Integration**
- **Voice Chat Support**: Enhanced communication with Simple Voice Chat
- **Ambient Soundscapes**: Level-specific atmospheric audio
- **Entity Audio**: Unique sounds for each creature
- **Interactive Audio**: Tape recorder playback system

---

## 🏗️ Complete Block List

### 🏨 **furniture Blocks**
- **Bed 1 & Bed 2**: Different bed styles for various room types
- **Table**: Furniture for hotel rooms and common areas
  - (more may come soon)

### 🔧 **Utility Blocks**
- **Tape Recorder**: Interactive audio playback device
- **Vent**: Crawlable ventilation systems for stealth gameplay
- **Emergency Lights**: Battery-powered lighting systems

---

## 🎮 Advanced Gameplay Mechanics

### 🏃 **Level RUN Challenge System**
- **Distance-Based Escape**: Must travel exactly 200 blocks to trigger exit
- **Health Pressure**: Continuous damage (0.5 hearts every 3 seconds)
- **Real-Time Tracking**: Progress displayed in action bar every 10 seconds
- **Random Destinations**: Escape leads to Poolrooms, Level 0, or Level 1
- **Entity Spawning**: Smilers spawn every 40 ticks to increase difficulty
- **One-Time Event**: Level RUN can only be triggered once per server session



### 🌐 **Multiplayer Features**
- **Synchronized Systems**: All players share the same survival mechanics
- **Server Control**: Administrators have full control over all features
- **Performance Optimized**: Designed to handle multiple players efficiently
- **Voice Integration**: Enhanced communication for team survival

### 🎨 **Customization Options**
- **HUD Positioning**: Adjust meter locations on screen
- **Color Themes**: Multiple color schemes for visual elements
- **Audio Levels**: Individual volume controls for different sound types
- **Control Bindings**: Fully customizable key mappings

---

## 🔮 Future Updates & Roadmap

### 📅 **Planned Features**
- **Additional Levels**: More Backrooms levels with unique mechanics
- **Expanded Lore System**: More tape recordings and story elements
- **New Entities**: Additional creatures with unique behaviors
- **Enhanced Audio**: Improved voice chat integration and ambient sounds

### 🎯 **Community Requests**
- **Level Builder Tools**: Tools for creating custom Backrooms levels
- **Entity Customization**: Options for server owners to modify entity behavior
- **Advanced HUD Options**: More customization for visual elements

# (the addon is still under development and is not yet fully optimized so be warned t have errors!)

---

*The Volatile Addon - The never ending expansion.*
