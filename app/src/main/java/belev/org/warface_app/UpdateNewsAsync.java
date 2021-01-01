package belev.org.warface_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import belev.org.warface_app.data.DataContract;
import belev.org.warface_app.data.DataDbHelper;

public class UpdateNewsAsync extends AsyncTask {

    Context context;
    List<News> newsArray;

    public UpdateNewsAsync(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        parseNews();
        insertNewsToDatabase();

        return null;
    }

    public void parseNews() {
        NewsParser newsParser = new NewsParser();
        newsArray = newsParser.pars();
    }

    public String cleanText(String text, String imageLink) {
        StringBuilder stringBuilder = new StringBuilder(text);
        String html = "<html>";
        String style = "<link type=\"text/css\" rel=\"stylesheet\" media=\"all\" href=\"https://edgenews.ru/android/wardocwarface/news/style.css\">";
        String image = "<img src=\"" + imageLink + "\">";

        stringBuilder.insert(0, html);
        stringBuilder.insert(6, style);
        stringBuilder.insert(121, image);
        stringBuilder.append("</html>");

        String cleanTags = stringBuilder
                .toString()
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&nbsp;", " ")
                .replace("&quot;", "\"")
                .replace("&mdash;", "—")
        	    .replace("<!--break-->", "");

        Pattern pattern = Pattern.compile("<script(.*?)</script>(.*?)<style(.*?)</style>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(cleanTags);
        String cleanScript = matcher.replaceAll("");

        return cleanScript;
    }

    public String cleanPreview(String text) {
        String cleanText = text
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&nbsp;", " ")
                .replace("&quot;", "\"")
                .replace("&mdash;", "—")
                .replace("<!--break-->", "");

        return cleanText;
    }

    public void insertNewsToDatabase() {
        DataDbHelper dbHelper = new DataDbHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        for (News news : newsArray) {
            if (! isNewsExists(news, sqLiteDatabase)) {
                String text = cleanText(news.getText(), news.getImage());
                news.setText(text);

                String previewText = cleanPreview(news.getPreviewText());
                news.setPreviewText(previewText);

                String date = News.formatFromParseToDatabase(news.getDate());
                news.setDate(date);

                NewsValuesAdapter newValuesAdapter = new NewsValuesAdapter(news);
                ContentValues contentValues = newValuesAdapter.convert();
                long newRowId = sqLiteDatabase.insert(DataContract.NewsEntry.TABLE_NAME, null, contentValues);
            }
        }
    }

    public boolean isNewsExists(News news, SQLiteDatabase sqLiteDatabase) {
        String selection = DataContract.NewsEntry.COLUMN_TITLE + " = ?";
        String[] selectionArgs = { news.getTitle() };

        Cursor cursor = sqLiteDatabase.query(DataContract.NewsEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToNext()) {
            // Log.e("CustomLogTag", "Entry exists in database");
            cursor.close();
            return true;
        } else {
            // Log.e("CustomLogTag", "Entry do not exists in database");
            cursor.close();
            return false;
        }
    }
}
