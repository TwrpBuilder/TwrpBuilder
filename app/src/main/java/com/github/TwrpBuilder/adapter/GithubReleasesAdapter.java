package com.github.TwrpBuilder.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.util.ReleaseTools;

/**
 * Created by: veli
 * Date: 10/25/16 10:13 PM
 */

public class GithubReleasesAdapter extends GithubAdapterIDEA
{
	private boolean mShowBetaUpdates = false;
	private String mDeviceCodeName;

	public GithubReleasesAdapter(Context context)
	{
		super(context);
	}

	@Override
	protected void onUpdate(JSONArray list)
	{
		mShowBetaUpdates = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("show_beta_updates", false);
		mDeviceCodeName = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("device_code_name", "null");

		JSONArray newList = new JSONArray();

		for (int i = 0; i < list.length(); i++)
		{
			try
			{
				final JSONObject release = list.getJSONObject(i);

				if ((release.getBoolean("prerelease") && !mShowBetaUpdates) || (release.has("tag_name") && release.getString("tag_name").startsWith("@") && !release.getString("tag_name").startsWith("@" + mDeviceCodeName + ",")))
				{}
				else
				{
					newList.put(release);
				}
			} catch (JSONException e) {}
		}
		super.onUpdate(newList);
	}

	@Override
	protected View onView(int position, View convertView, ViewGroup parent)
	{
		final JSONObject release = (JSONObject) getItem(position);

		if (convertView == null)
			convertView = mInflater.inflate(R.layout.list_release, parent, false);

		TextView text1 = convertView.findViewById(R.id.list_release_text1);
		TextView text2 = convertView.findViewById(R.id.list_release_text2);
		TextView betaWarningText = convertView.findViewById(R.id.list_release_beta_release_warning);
		final Button actionButton = convertView.findViewById(R.id.list_release_action_button);

		convertView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				actionButton.setVisibility((actionButton.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);
			}
		});

		try
		{
			String title = "";

			if (release.getBoolean("prerelease"))
				betaWarningText.setVisibility(View.VISIBLE);

			if (release.has("name"))
				title += release.getString("name");

			if (release.has("tag_name"))
			{
				String tag = release.getString("tag_name");

				if (tag.startsWith("@"))
				{
					int positionS = tag.indexOf(",");

					if (positionS != -1)
						tag = tag.substring(positionS + 1, tag.length());
				}

				title += " " + tag;
			}
			text1.setText(title);

			if (release.has("body"))
				text2.setText(release.getString("body"));

			if (release.has("assets"))
			{
				JSONArray assets = release.getJSONArray("assets");

				if (assets.length() > 0)
				{
					final JSONObject firstAsset = assets.getJSONObject(0);
					final String fileName = firstAsset.getString("name");
					final long fileId = firstAsset.getLong("id");
					final long fileSize = firstAsset.getLong("size");

					final File fileIns = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileId + "-" + fileName);

					if (fileIns.isFile())
					{
						if (fileIns.length() == fileSize)
						{
							actionButton.setText(R.string.install);

							actionButton.setOnClickListener(new View.OnClickListener()
							{
								@Override
								public void onClick(View v)
								{
									ReleaseTools.openFile(getContext(), fileIns);
								}
							});
						}
						else
							actionButton.setEnabled(false);

					}
					else
					{
						if (firstAsset.has("browser_download_url"))
						{
							actionButton.setOnClickListener(new View.OnClickListener()
							{
								@Override
								public void onClick(View v)
								{
									DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(getContext().DOWNLOAD_SERVICE);

									try
									{
										Uri uri = Uri.parse(firstAsset.getString("browser_download_url"));
										DownloadManager.Request request = new DownloadManager.Request(uri);
										request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileId + "-" + fileName);

										request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
										Long reference = downloadManager.enqueue(request);

										actionButton.setEnabled(false);
									} catch (JSONException e)
									{
										e.printStackTrace();
									}
								}
							});
						}
					}

				}
			}

		} catch (JSONException e)
		{
			e.printStackTrace();
		}

		return convertView;
	}
}