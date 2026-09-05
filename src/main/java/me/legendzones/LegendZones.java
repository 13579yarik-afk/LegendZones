package me.legendzones;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

public class LegendZones extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("LegendZones включён!");
    }

    @Override
    public void onDisable() {
        getLogger().info("LegendZones выключен!");
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!command.getName().equalsIgnoreCase("zoneset")) {
            return false;
        }

        if (!sender.hasPermission("legendzones.zoneset")) {
            sender.sendMessage(
                    ChatColor.RED + "У тебя нет прав!"
            );
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(
                    ChatColor.RED +
                    "Используй: /zoneset <зона> [игрок]"
            );
            return true;
        }

        String zone = args[0].toUpperCase();

        if (!getConfig().contains("sets." + zone)) {
            sender.sendMessage(
                    ChatColor.RED +
                    "Такой зоны нет!"
            );
            return true;
        }

        Player target;

        if (args.length >= 2) {

            target = getServer().getPlayer(args[1]);

            if (target == null) {
                sender.sendMessage(
                        ChatColor.RED +
                        "Игрок не найден или не находится онлайн!"
                );
                return true;
            }

        } else {

            if (!(sender instanceof Player)) {
                sender.sendMessage(
                        ChatColor.RED +
                        "Из консоли укажи игрока: /zoneset <зона> <игрок>"
                );
                return true;
            }

            target = (Player) sender;
        }

        int colorRGB =
                getConfig().getInt(
                        "sets." + zone + ".color"
                );

        /*
         * ==============================
         * БРОНЯ
         * ==============================
         */

        target.getInventory().setHelmet(
                createArmor(
                        Material.LEATHER_HELMET,
                        colorRGB,
                        "Шлем",
                        zone
                )
        );

        target.getInventory().setChestplate(
                createArmor(
                        Material.LEATHER_CHESTPLATE,
                        colorRGB,
                        "Нагрудник",
                        zone
                )
        );

        target.getInventory().setLeggings(
                createArmor(
                        Material.LEATHER_LEGGINGS,
                        colorRGB,
                        "Поножи",
                        zone
                )
        );

        target.getInventory().setBoots(
                createArmor(
                        Material.LEATHER_BOOTS,
                        colorRGB,
                        "Ботинки",
                        zone
                )
        );

        /*
         * ==============================
         * ПРЕДМЕТ ЗОНЫ
         * ==============================
         */

        int weaponId =
                getConfig().getInt(
                        "sets." + zone + ".weapon-id"
                );

        short weaponData =
                (short) getConfig().getInt(
                        "sets." + zone + ".weapon-data"
                );

        String weaponName =
                getConfig().getString(
                        "sets." + zone + ".weapon-name"
                );

        Material weaponMaterial =
                Material.getMaterial(weaponId);

        if (weaponMaterial != null) {

            ItemStack weapon =
                    new ItemStack(
                            weaponMaterial,
                            1,
                            weaponData
                    );

            ItemMeta meta =
                    weapon.getItemMeta();

            if (meta != null) {

                meta.setDisplayName(
                        weaponName
                );

                weapon.setItemMeta(meta);
            }

            weapon.addUnsafeEnchantment(
                    Enchantment.DAMAGE_ALL,
                    1
            );

            weapon.addUnsafeEnchantment(
                    Enchantment.DURABILITY,
                    1
            );

            target.getInventory().addItem(
                    weapon
            );
        }

        sender.sendMessage(
                ChatColor.GREEN +
                "✦ Сет " +
                zone +
                " выдан игроку " +
                target.getName() +
                "!"
        );

        return true;
    }

    /*
     * ==============================
     * СОЗДАНИЕ БРОНИ
     * ==============================
     */

    private ItemStack createArmor(
            Material material,
            int rgb,
            String type,
            String zone
    ) {

        ItemStack item =
                new ItemStack(material);

        LeatherArmorMeta meta =
                (LeatherArmorMeta) item.getItemMeta();

        if (meta != null) {

            meta.setColor(
                    Color.fromRGB(rgb)
            );

            meta.setDisplayName(
                    ChatColor.WHITE +
                    "✦ " +
                    type +
                    " " +
                    zone +
                    " ✦"
            );

            item.setItemMeta(meta);
        }

        return item;
    }

    /*
     * ==============================
     * ГЛАВНАЯ ПРОВЕРКА
     * ==============================
     *
     * Проверяем именно кнопку покупки
     * и именно надетую броню игрока.
     *
     * SPRING -> CUPCAKE
     * PURPIE_GUY -> SPRING
     * BALLON_BOY -> PURPIE_GUY
     * GFD -> BALLON_BOY
     * MANGLE -> GFD
     * PUPPET -> MANGLE
     * FREDDY -> PUPPET
     * FOXY -> FREDDY
     * CHICA -> FOXY
     * BONNIE -> CHICA
     *
     * CUPCAKE ничего не требует.
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onZoneMenuClick(
            InventoryClickEvent event
    ) {

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player =
                (Player) event.getWhoClicked();

        /*
         * Нас интересует только верхнее меню.
         */
        if (event.getClickedInventory() == null) {
            return;
        }

        if (event.getClickedInventory()
                != player.getOpenInventory().getTopInventory()) {
            return;
        }

        String title =
                event.getView().getTitle();

        if (title == null) {
            return;
        }

        String cleanTitle =
                ChatColor.stripColor(title);

        if (cleanTitle == null) {
            return;
        }

        cleanTitle =
                cleanTitle.toUpperCase();

        String currentZone =
                getMenuZone(cleanTitle);

        if (currentZone == null) {
            return;
        }

        /*
         * CUPCAKE покупается без предыдущей брони.
         */
        if (currentZone.equals("CUPCAKE")) {
            return;
        }

        /*
         * Определяем кнопку по её названию.
         *
         * Это надёжнее, чем просто смотреть
         * на номер слота.
         */
        ItemStack clicked =
                event.getCurrentItem();

        if (clicked == null ||
                clicked.getType() == Material.AIR) {
            return;
        }

        if (!clicked.hasItemMeta()) {
            return;
        }

        ItemMeta clickedMeta =
                clicked.getItemMeta();

        if (clickedMeta == null ||
                !clickedMeta.hasDisplayName()) {
            return;
        }

        String buttonName =
                ChatColor.stripColor(
                        clickedMeta.getDisplayName()
                );

        if (buttonName == null) {
            return;
        }

        String armorType =
                getArmorTypeFromButton(buttonName);

        /*
         * Если это не одна из четырёх частей
         * брони — ничего не блокируем.
         *
         * Поэтому оружие продолжает работать.
         */
        if (armorType == null) {
            return;
        }

        String previousZone =
                getPreviousZone(currentZone);

        if (previousZone == null) {
            return;
        }

        /*
         * Получаем именно надетую часть брони.
         */
        ItemStack requiredArmor =
                getArmorItem(
                        player,
                        armorType
                );

        /*
         * ПРОВЕРКА НАЗВАНИЯ.
         */
        if (!isCorrectZoneArmor(
                requiredArmor,
                armorType,
                previousZone
        )) {

            /*
             * Полностью отменяем клик.
             *
             * ChestCommands не должен списать
             * предметы и не должен выполнить
             * COMMAND этой кнопки.
             */
            event.setCancelled(true);

            player.updateInventory();

            player.sendMessage(
                    ChatColor.RED +
                    "✖ Для получения " +
                    currentZone +
                    " нужна " +
                    armorType +
                    " " +
                    previousZone +
                    "!"
            );

            return;
        }
    }

    /*
     * ==============================
     * ОПРЕДЕЛЕНИЕ ТИПА БРОНИ
     * ==============================
     */

    private String getArmorTypeFromButton(
            String name
    ) {

        String upper =
                name.toUpperCase();

        if (upper.contains("ШЛЕМ")) {
            return "Шлем";
        }

        if (upper.contains("НАГРУДНИК")) {
            return "Нагрудник";
        }

        if (upper.contains("ПОНОЖИ")) {
            return "Поножи";
        }

        if (upper.contains("БОТИНКИ")) {
            return "Ботинки";
        }

        return null;
    }

    /*
     * ==============================
     * ОПРЕДЕЛЕНИЕ ЗОНЫ МЕНЮ
     * ==============================
     */

    private String getMenuZone(
            String title
    ) {

        if (title.contains("CUPCAKE")) {
            return "CUPCAKE";
        }

        if (title.contains("SPRING")) {
            return "SPRING";
        }

        if (title.contains("PURPIE GUY")) {
            return "PURPIE_GUY";
        }

        if (title.contains("BALLON BOY")) {
            return "BALLON_BOY";
        }

        if (title.contains("GFD")) {
            return "GFD";
        }

        if (title.contains("MANGLE")) {
            return "MANGLE";
        }

        if (title.contains("PUPPET")) {
            return "PUPPET";
        }

        if (title.contains("FREDDY")) {
            return "FREDDY";
        }

        if (title.contains("FOXY")) {
            return "FOXY";
        }

        if (title.contains("CHICA")) {
            return "CHICA";
        }

        if (title.contains("BONNIE")) {
            return "BONNIE";
        }

        return null;
    }

    /*
     * ==============================
     * ПРЕДЫДУЩАЯ ЗОНА
     * ==============================
     */

    private String getPreviousZone(
            String zone
    ) {

        switch (zone) {

            case "SPRING":
                return "CUPCAKE";

            case "PURPIE_GUY":
                return "SPRING";

            case "BALLON_BOY":
                return "PURPIE_GUY";

            case "GFD":
                return "BALLON_BOY";

            case "MANGLE":
                return "GFD";

            case "PUPPET":
                return "MANGLE";

            case "FREDDY":
                return "PUPPET";

            case "FOXY":
                return "FREDDY";

            case "CHICA":
                return "FOXY";

            case "BONNIE":
                return "CHICA";

            default:
                return null;
        }
    }

    /*
     * ==============================
     * ПОЛУЧЕНИЕ БРОНИ
     * ==============================
     */

    private ItemStack getArmorItem(
            Player player,
            String armorType
    ) {

        if (armorType.equals("Шлем")) {

            return player
                    .getInventory()
                    .getHelmet();
        }

        if (armorType.equals("Нагрудник")) {

            return player
                    .getInventory()
                    .getChestplate();
        }

        if (armorType.equals("Поножи")) {

            return player
                    .getInventory()
                    .getLeggings();
        }

        if (armorType.equals("Ботинки")) {

            return player
                    .getInventory()
                    .getBoots();
        }

        return null;
    }

    /*
     * ==============================
     * ПРОВЕРКА НАЗВАНИЯ БРОНИ
     * ==============================
     */

    private boolean isCorrectZoneArmor(
            ItemStack item,
            String armorType,
            String zone
    ) {

        if (item == null) {
            return false;
        }

        if (item.getType() == Material.AIR) {
            return false;
        }

        /*
         * Проверяем материал.
         */
        Material expectedMaterial;

        if (armorType.equals("Шлем")) {

            expectedMaterial =
                    Material.LEATHER_HELMET;

        } else if (armorType.equals("Нагрудник")) {

            expectedMaterial =
                    Material.LEATHER_CHESTPLATE;

        } else if (armorType.equals("Поножи")) {

            expectedMaterial =
                    Material.LEATHER_LEGGINGS;

        } else if (armorType.equals("Ботинки")) {

            expectedMaterial =
                    Material.LEATHER_BOOTS;

        } else {

            return false;
        }

        if (item.getType() != expectedMaterial) {
            return false;
        }

        /*
         * Проверяем ItemMeta.
         */
        if (!item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return false;
        }

        /*
         * Проверяем DisplayName.
         */
        if (!meta.hasDisplayName()) {
            return false;
        }

        String actualName =
                ChatColor.stripColor(
                        meta.getDisplayName()
                );

        if (actualName == null) {
            return false;
        }

        /*
         * Какое название должно быть.
         *
         * Например:
         *
         * ✦ Нагрудник CUPCAKE ✦
         */
        String expectedName =
                "✦ " +
                armorType +
                " " +
                zone +
                " ✦";

        return actualName.equalsIgnoreCase(
                expectedName
        );
    }
}
