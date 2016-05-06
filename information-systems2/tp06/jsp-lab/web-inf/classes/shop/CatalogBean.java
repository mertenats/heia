/*---------------------------------------------------
 *  COFFEE SHOP - CatalogBean (B)
 *  This bean holds all available products.
 *  Each catalog item represents an article.
 *---------------------------------------------------
 * HEIA-FR / R. Scheurer (2015-16)
 *---------------------------------------------------*/
package shop;

import java.util.Enumeration;
import java.util.Hashtable;

public class CatalogBean {

  private Hashtable<Integer, CatalogItem> catalog = new Hashtable<Integer, CatalogItem>();

  public CatalogBean() {
    catalog.put(new Integer(1001), new CatalogItem(1001, "100g Java Bean", 88));
    catalog.put(new Integer(1002), new CatalogItem(1002, "1kg Coffee Bean", 9));
    catalog.put(new Integer(1003),
        new CatalogItem(1003, "1x Mister Bean", 90000));
    catalog.put(new Integer(1004), new CatalogItem(1004, "1kg Chili Bean", 10));
    catalog.put(new Integer(1005),
        new CatalogItem(1005, "1kg Vanilla Bean", 20));
  }

  public CatalogItem getCatalogItem(int id) {
    return catalog.get(new Integer(id));
  }

  public void addCatalogItem(int Id, String name, Integer price) {
    catalog.put(new Integer(Id), new CatalogItem(Id, name, price));
  }

  public Enumeration<CatalogItem> getCatalogItems() {
    return catalog.elements();
  }

  public String toString() {
    return catalog.toString();
  }

  // Usage of enumerations:
  // ----------------------
  //
  // CatalogBean catalog = ...;
  // Enumeration<CatalogItem> catalogItems = catalog.getCatalogItems();
  // CatalogItem item = null;
  // while(catalogItems.hasMoreElements()) {
  // item = catalogItems.nextElement();
  // ...
  // }

}
