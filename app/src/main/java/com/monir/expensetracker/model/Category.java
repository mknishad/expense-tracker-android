package com.monir.expensetracker.model;


public class Category {

  private int categoryId;
  private String categoryName;

  public Category(int categoryId, String categoryName) {
    this.categoryId = categoryId;
    this.categoryName = categoryName;
  }

  public Category(String categoryName) {
    this.categoryName = categoryName;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }
}
