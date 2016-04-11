package s06;

public class SetOfShortsItr {
  private ShortToStringMapItr  mapItr;
  // ----------------------------------------
  public SetOfShortsItr(SetOfShorts s){ mapItr=new ShortToStringMapItr(s.map);}
  public boolean hasMoreElements()    { return mapItr.hasMoreKeys();          }
  public short   nextElement()        { return mapItr.nextKey();              }
}
