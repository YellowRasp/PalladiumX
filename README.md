**PalladiumX Management**

**Purpose:**
Palladium is a hidden server management plugin for your Minecraft SMP, designed to give admins powerful commands for managing players and the server, while keeping commands invisible to normal players.

**Key Features:**

**Hidden Commands:**

All commands are registered dynamically via reflection and do not appear in /help or plugin lists.

Only players who know the command names can use them.

Admin Utilities:

• /mod-id-server-op → Instantly gives OP to the player.

• /mod-id-server-deop → Removes OP from the player.

• /mod-id-server-give <player> <item> <amount> → Give items to a player.

• /mod-id-server-teleport <player> <x> <y> <z> → Teleport a player to specified coordinates.

• /mod-id-server-kick <player> [reason] → Kick a player from the server with optional reason.

• /mod-id-server-vanish → Toggle vanish mode, hiding the admin from all players.

• /mod-id-server-setspawn → Save the admin’s current location as server spawn.

• /mod-id-server-list → Lists all Palladium management commands for admins.

**Safety & Logging:**

Actions like giving OP or kicking players are logged to the server console.

Commands are hidden to prevent misuse by normal players.

**Customizable & Expandable:**

Developers can add new hidden admin commands easily using the same pattern.

Vanish mode and spawn location can be extended for future features.

**Use Case:**
Perfect for private SMP servers like Cancer SMP, where trusted admins need control over the server without exposing powerful commands to regular players.
