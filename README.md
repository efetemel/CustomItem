
# Custom Item Plugin
Minecraft 1.20 Sürümü için geliştirmiş olduğum Custom Item plugini.

## Gereksinimler
Minecraft Sunucunuz Spigot olması önerilir.

## Kurulum
Sunucunuzun Plugins klasörüne CustomItem.jar dosyasını atmanız yeterlidir.
- Config.yml dosyasını kendiniz oluşturabilir veya buradan alabilirsiniz.

## Config Ayarlama
- Yeni bir item eklemek için config de örnek olarak verilen itemlerden baz alabilirsiniz.
- Eklediğiniz itemin craftında eğer bir custom item varsa "custom_craft_item" değerini "true" olarak işaretleyin.
- Craft Items kısmında ".customItemAdı" şeklinde istediğiniz item in "short_name" ini başına "." olarak ekleyin.
- Yeni bir item eklediğinizde /creload ile yeni itemi oyuna ekleyin.
- Effect kısmına istediğiniz efekti yazıp value değerine saniye yazın.
- Sigara çeşidi eklemek için "short_name" kısmına "cigaratte_malbora" gibi başına "cigaratte" ekleyin.
- Sigara çeişidinde Hak sistemi için "Kalan Hak: 3" gibi bir değer verin.
- Ölümsüzlük totemi eklemek istiyorsanız "short_name" kısmı "undying" içermesi gerekiyor ve "item_id" "ENCHANTED_BOOK" olmasını öneririm.
- Custom Texture eklemek için "custom_model_data" kısmına istediğinz bir sayı girin ve texture pack dosyasında o sayıya karşılık gelen texture ekleyin.

## Oyun içi Ekran Görüntüleri

![App Screenshot](https://i.hizliresim.com/ga1lp0i.png)

## Örnek Config.yml
```yml
goldbread:
    display_name: "Altın Ekmek"
    short_name: "goldbread"
    lore:
      - "Altınla ekmeğin muhteşem birleşimi"
    item_id: "BREAD"
    craft_type:
      top_left: "A"
      top_center: "A"
      top_right: "A"
      center_left: "A"
      center_center: "B"
      center_right: "A"
      bottom_left: "A"
      bottom_center: "A"
      bottom_right: "A"
    craft_items:
      A: "GOLD_INGOT"
      B: "BREAD"
    custom_craft_item: false
    custom_model_data: 23
  pita:
    display_name: "Ramazan Pidesi"
    short_name: "pita"
    lore:
      - "11 Ayın Sultanı Ramazan"
      - "İftarda çok güzel gider"
      - "Altınlı"
    item_id: "BREAD"
    craft_type:
      top_left: "A"
      top_center: "A"
      top_right: "A"
      center_left: "B"
      center_center: "B"
      center_right: "B"
      bottom_left: "C"
      bottom_center: "C"
      bottom_right: "C"
    craft_items:
      A: "GOLD_INGOT"
      B: "SUGAR"
      C: ".goldbread"
    effects:
      SATURATION: 120
    custom_craft_item: true
    custom_model_data: 7
```
