package SalaciousServer.network.webserver.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import zombie.core.Translator;
import zombie.scripting.ScriptManager;

public class ItemDao {
  public Iterable<Item> getAllItems() {
    List<Item> items = new ArrayList<Item>();
    for (zombie.scripting.objects.Item item : ScriptManager.instance.getAllItems()) {
      items.add(
          new Item(
              item.getFullName(),
              item.DisplayName,
              item.Tooltip == null ? "" : Translator.getText(item.Tooltip)));
    }
    return items;
  }

  public Item getItemByName(String name) {
    zombie.scripting.objects.Item item = ScriptManager.instance.getItem(name);
    return new Item(
        item.getFullName(),
        item.DisplayName,
        item.Tooltip == null ? "" : Translator.getText(item.Tooltip));
  }

  public Item getRandomItem() {
    ArrayList<zombie.scripting.objects.Item> items = ScriptManager.instance.getAllItems();
    zombie.scripting.objects.Item item = items.get(new Random().nextInt(items.size()));
    return new Item(
        item.getFullName(),
        item.DisplayName,
        item.Tooltip == null ? "" : Translator.getText(item.Tooltip));
  }
}
