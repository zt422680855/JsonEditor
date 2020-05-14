/**
 * @Description:
 * @Author: zhengtao
 * @CreateDate: 2020/5/7 17:48
 */
public class JsonFormatter {
    private static final int INDENT_SPACE = 2;

    public JsonFormatter() {
    }

    public String format(String text) {
        StringBuilder formattedText = new StringBuilder();
        int indentCount = 0;
        char[] var4 = text.toCharArray();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            char c = var4[var6];
            if (c != '[' && c != '{') {
                if (c == ',') {
                    formattedText.append(c);
                    formattedText.append('\n');
                    this.appendIndent(formattedText, indentCount);
                } else if (c != ']' && c != '}') {
                    formattedText.append(c);
                } else {
                    formattedText.append('\n');
                    --indentCount;
                    this.appendIndent(formattedText, indentCount);
                    formattedText.append(c);
                }
            } else {
                formattedText.append(c);
                formattedText.append('\n');
                ++indentCount;
                this.appendIndent(formattedText, indentCount);
            }
        }

        return formattedText.toString();
    }

    private void appendIndent(StringBuilder text, int indentCount) {
        for(int i = 0; i < indentCount * 2; ++i) {
            text.append(' ');
        }

    }
}
