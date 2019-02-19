package com.kealliang.base.search;

/**
 * @author lsr
 * @ClassName HouseSuggest
 * @Date 2019-02-11
 * @Desc 搜索输入建议
 * @Vertion 1.0
 */
public class HouseSuggest {
    private String input;
    private int weight = 10; // 权重默认为10

    public HouseSuggest(String input, int weight) {
        this.input = input;
        this.weight = weight;
    }

    public HouseSuggest(String input) {
        this.input = input;
    }

    public HouseSuggest() {
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
