package com.ly.orm;
import com.ly.linq.Enm;
public class Sql { Sql(String s) {  sql = s; }
 public Sql select(String... cols) {  if (cols.length == 0) {   return new Sql(String.format(this.sql, "*"));  }  return new Sql(String.format(this.sql, Enm.toString(cols, ','))); }
 public static F from(String f) {  return new F(f); }
 public String toString() {  return sql; }
 String sql;
 public static class F extends Sql {
  F(String s) {   super("select %s from " + s);  }
  public F join(String j) {   return new F(j);  }
  public W where(String w) {   return new W("where " + w);  }
  public G groupby(String g) {   return new G("group by " + g);  }
  public Sql orderby(String o) {   return new Sql("order by " + o);  }
 }
 public static class W extends Sql {
  W(String w) {   super(w);  }
  public G groupby(String g) {   return new G(sql + " group by " + g);  } }
 public static class G extends Sql {
  G(String g) {   super(g);  }
  public H having(String h) {   return new H(this.sql + " having " + h);  }
  public static class H extends Sql {
   H(String h) {    super(h);   }
   public Sql orderby(String o) {    return new Sql(sql + " order by " + o);   }  } }}
