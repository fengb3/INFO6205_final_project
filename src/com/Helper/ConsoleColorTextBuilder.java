package com.Helper;

/*
 * This class is for printing color text in console
 * Reference: https://www.jianshu.com/p/ed5147cebf9b
 */
public class ConsoleColorTextBuilder {

    public static ColorBuilder with(String content) {
        return new Config(content);
    }

    public interface Builder {

        public Builder append(ColorBuilder next);

        public String build();

    }

    public interface ColorBuilder  extends Builder{

        public ColorBuilder red();

        public ColorBuilder green();

        public ColorBuilder yellow();

        public ColorBuilder blue();

        public ColorBuilder purple();

        public ColorBuilder cyan();

        public ColorBuilder gray();

        public ColorBuilder background(Color color);

        /**
         * bound
         * @return
         */
        public ColorBuilder bound();

        /**
         * underline
         * @return
         */
        public ColorBuilder underLine();

        /**
         * italic
         * @return
         */
        public ColorBuilder italic();

    }


    private static class Config implements ColorBuilder {
        private StringBuilder builder;
        private String content;
        private boolean end;
        private Config(String content) {
            this.builder = new StringBuilder("\033[1");
            this.content = content;
        }

        @Override
        public ColorBuilder red() {
            color(Color.RED);
            return this;
        }

        @Override
        public ColorBuilder green() {
            color(Color.GREEN);
            return this;
        }

        @Override
        public ColorBuilder yellow() {
            color(Color.YELLOW);
            return this;
        }

        @Override
        public ColorBuilder blue() {
            color(Color.BLUE);
            return this;
        }

        @Override
        public ColorBuilder purple() {
            color(Color.PURPLE);
            return this;
        }

        @Override
        public ColorBuilder cyan() {
            color(Color.CYAN);
            return this;
        }

        @Override
        public ColorBuilder gray() {
            color(Color.GRAY);
            return this;
        }

        @Override
        public Builder append(ColorBuilder next) {
            this.end().append(next.build());
            return this;
        }

        private Config color(Color color) {
            builder.append(";3").append(color.code);
            return this;
        }

        @Override
        public ColorBuilder bound() {
            builder.append(";51");
            return this;
        }

        @Override
        public ColorBuilder underLine() {
            builder.append(";21");
            return this;
        }

        @Override
        public ColorBuilder italic() {
            builder.append(";3");
            return this;
        }

        @Override
        public ColorBuilder background(Color color) {
            builder.append(";4").append(color.code);
            return this;
        }


        private StringBuilder end() {
            end = true;
            this.builder.append("m").append(this.content).append("\33[m");
            return builder;
        }

        @Override
        public String build() {
            if (!end) {
                this.builder.append("m").append(this.content).append("\33[m");
            }
            return this.builder.toString();
        }
    }

    public enum Color {
        RED("1"),
        GREEN("2"),
        YELLOW("3"),
        BLUE("4"),
        PURPLE("5"),
        CYAN("6"),
        GRAY("7");

        Color(String code) {
            this.code = code;
        }
        private String code;
    }
}
