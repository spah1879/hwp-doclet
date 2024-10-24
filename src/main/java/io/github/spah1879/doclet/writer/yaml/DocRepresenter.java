package io.github.spah1879.doclet.writer.yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;;

public class DocRepresenter extends Representer {

  public DocRepresenter(DumperOptions options) {
    super(options);
  }

  @Override
  protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue,
      Tag customTag) {
    NodeTuple defaultNode = super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);

    StringBuilder kebabBuilder = new StringBuilder();
    property.getName().chars().forEach(i -> {
      char c = (char) i;
      if (Character.isUpperCase(c)) {
        kebabBuilder.append("-").append(Character.toLowerCase(c));
      } else {
        kebabBuilder.append(c);
      }
    });

    return new NodeTuple(representData(kebabBuilder.toString()), defaultNode.getValueNode());
  }

  @Override
  protected Set<Property> getProperties(Class<? extends Object> type) {
    Set<Property> propertySet;
    if (typeDefinitions.containsKey(type)) {
      propertySet = typeDefinitions.get(type).getProperties();
    } else {
      propertySet = getPropertyUtils().getProperties(type);
    }

    List<Property> propsList = new ArrayList<>(propertySet);
    Collections.sort(propsList, new BeanPropertyComparator());

    return new LinkedHashSet<>(propsList);
  }

  private static final List<String> ORDERS = List.of(
      "packageName",
      "name",
      "modifiers",
      "type",
      "returnType",
      "parameters",
      "flatSignature",
      "tags",
      "comment",
      "fields",
      "constructors",
      "methods",
      "value",
      "full",
      "simple",
      "firstSentence",
      "body");

  class BeanPropertyComparator implements Comparator<Property> {
    public int compare(Property p1, Property p2) {

      final String p1Name = p1.getName();
      final String p2Name = p2.getName();
      int p1Order = Integer.MAX_VALUE;
      int p2Order = Integer.MAX_VALUE;

      for (int i = 0; i < ORDERS.size(); i++) {
        String order = ORDERS.get(i);
        if (order.equals(p1Name)) {
          p1Order = i;
        } else if (order.equals(p2Name)) {
          p2Order = i;
        }
      }

      if (p1Order != Integer.MAX_VALUE || p2Order != Integer.MAX_VALUE) {
        return p1Order - p2Order;
      } else {
        return p1Name.compareTo(p2Name);
      }
    }
  }
}