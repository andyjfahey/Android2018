package uk.co.healtht.healthtouch.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.api.ApiCache;
import uk.co.healtht.healthtouch.api.EndPointProvider;
import uk.co.healtht.healthtouch.api.EndPointRequest;
import uk.co.healtht.healthtouch.model.delegate.HTMedicationImageDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTMedicationTakenTrackerDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerMedicationDelegate;
import uk.co.healtht.healthtouch.model.entities.HTMedicationImage;
import uk.co.healtht.healthtouch.model.entities.HTMedicationTakenTracker;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMedication;
import uk.co.healtht.healthtouch.network.synchronization.SyncDb;
import uk.co.healtht.healthtouch.proto.Medication;
import uk.co.healtht.healthtouch.proto.UserReply;
import uk.co.healtht.healthtouch.utils.TextUtils;
import uk.co.healtht.healthtouch.utils.ViewUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

public class MedicationEditorFragment extends BaseFragment
{
	// Hold a reference to the current animator,
	// so that it can be canceled mid-way.
	private Animator mCurrentAnimator;
	// The system "short" animation time duration, in milliseconds. This
	// duration is ideal for subtle animations or animations that occur
	// very frequently.
	private int mShortAnimationDuration;

	// Activity request codes
	private static final int REQUEST_TAKE_PHOTO = 100;
	private static final int REQUEST_SELECT_FILE = 100;
	//private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
	public static final int MEDIA_TYPE_IMAGE = 1;
	//public static final int MEDIA_TYPE_VIDEO = 2;

	// directory name to store captured images and videos
	private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

	private Uri fileUri; // file url to store image/video

	private HTTrackerMedication htTrackerMedication;
	private HTMedicationImage htMedicationImage;

	private SwitchCompat switchView;
    private TextView editName;
	private TextView editDosage;
	private ImageView medimage;
	private ImageView expandedImageView;

	// Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.


	private String base64Image;
	private String base64ImageOriginal;

	View.OnClickListener saveListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			validateAndPost();
		}
	};

	DialogInterface.OnClickListener NoDoseClick = new DialogInterface.OnClickListener()
	{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				HTMedicationTakenTrackerDelegate htMedicationTakenTrackerDelegate = new HTMedicationTakenTrackerDelegate();
				htMedicationTakenTrackerDelegate.add(getHTMedicationTakenTracker(0));

			}
	};

	DialogInterface.OnClickListener YesDoseClick = new DialogInterface.OnClickListener()
	{
		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			dialog.dismiss();
			HTMedicationTakenTrackerDelegate htMedicationTakenTrackerDelegate = new HTMedicationTakenTrackerDelegate();
			htMedicationTakenTrackerDelegate.add(getHTMedicationTakenTracker(1));
//			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//			LinearLayout layout = new LinearLayout(getContext());
//			layout.setOrientation(LinearLayout.VERTICAL);
//			layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(ActionBarOverlayLayout.LayoutParams.FILL_PARENT, LinearLayoutCompat.LayoutParams.FILL_PARENT));
//
//			TextView titleView = new TextView(getContext());
//			titleView.setText("Please select the date and time that the medication was taken.");
////			LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
////			titleView.setLayoutParams(lparams);
////			titleView.setTextAppearance(this, android.R.attr.textAppearanceLarge);
////			titleView.setText("Hallo Welt!");
//			layout.addView(titleView);
//
//			DatePicker picker = new DatePicker(getContext());
//			picker.setSpinnersShown(true);
//			TimePicker timePicker = new TimePicker(getContext());
//			timePicker.;
//			builder.setTitle("Dose Taken");
//			layout.addView(picker);
//			layout.addView(timePicker);
//
//			builder.setView(layout);
//
//			builder.setNegativeButton("Cancel", NoDoseClick);
//			builder.setPositiveButton("Save", NoDoseClick);
//
//			builder.show();
		}
	};

	private HTMedicationTakenTracker getHTMedicationTakenTracker(int taken)
	{
		HTMedicationTakenTracker htMedicationTakenTracker = new HTMedicationTakenTracker();
		htMedicationTakenTracker.medication_id =  htTrackerMedication.server_id;
		htMedicationTakenTracker.not_taken_reason ="";
		htMedicationTakenTracker.taken = taken;
		htMedicationTakenTracker.created_at = new Date(System.currentTimeMillis());
		htMedicationTakenTracker.updated_at = new Date(System.currentTimeMillis());
		htMedicationTakenTracker.server_id = null;
		htMedicationTakenTracker.synced = false;
		htMedicationTakenTracker.entity = "HTMedicationTakenTracker";
		return htMedicationTakenTracker;
	}

	@Override
	public void onFragmentStackUpdate(BaseFragment topFrag, BaseFragment parentFrag)
	{
		super.onFragmentStackUpdate(topFrag, parentFrag);
		setCustomActionListener(saveListener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_medication_editor, container, false);

		medimage = (ImageView) v.findViewById(R.id.med_image);
		expandedImageView = (ImageView) v.findViewById(R.id.expanded_image);

		editName = (TextView) v.findViewById(R.id.edit_name);
		editDosage = (TextView) v.findViewById(R.id.edit_dosage);

		switchView = (SwitchCompat) v.findViewById(R.id.edit_switch);
		switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				int colourId = isChecked ? R.color.label_color : R.color.light_gray;
				buttonView.setTextColor(buttonView.getResources().getColor(colourId));
			}
		});

		Bundle data = getArguments();

		htTrackerMedication = (HTTrackerMedication) data.getSerializable("medication");
		if (htTrackerMedication != null) {
			boolean isEditable = (htTrackerMedication.editable==null) ? true :(htTrackerMedication.editable==1);
			ViewUtils.setText(v, R.id.edit_name, htTrackerMedication.title);
			ViewUtils.setText(v, R.id.edit_dosage, htTrackerMedication.dosagedescription);
			switchView.setChecked((htTrackerMedication == null) ? true : htTrackerMedication.active == 1);
			switchView.setEnabled(isEditable);
			editName.setEnabled(isEditable);
			editDosage.setEnabled(isEditable);
			//getAllImages();
			htMedicationImage = getHTMedicationImage();
			if (htMedicationImage != null) {
				base64ImageOriginal = htMedicationImage.image;
				Bitmap bmp = base64ToBitmap(htMedicationImage.image);
				if (bmp != null)
					setMedImage(bmp);
			}

			TextView btnDoseTaken = (TextView)v.findViewById(R.id.btn_dose_taken);
			btnDoseTaken.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					TextUtils.showDoseTakenWorkflow(getActivity(), "activity", NoDoseClick, YesDoseClick);
				}
			});
		}
		else
		{
			TextView btnDoseTaken = (TextView)v.findViewById(R.id.btn_dose_taken);
			btnDoseTaken.setVisibility(View.INVISIBLE);
		}

		String title = data.getString("title");
		boolean isEditMode = (title != null);
		setTitle(isEditMode ? title : "ADD MEDICATION", R.color.light_carmine_pink, "Save");

		TextView btn_take_photo = (TextView)v.findViewById(R.id.btn_take_photo);

		medimage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//zoomImageFromThumb(medimage, expandedImageView, containerView);
				zoomImageFromThumb2(medimage, expandedImageView);
			}
		});

		btn_take_photo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// capture picture
				captureImage();
			}
		});

		TextView btnAddReminders = (TextView)v.findViewById(R.id.btn_add_reminder);
		boolean isEnabledForReminders = (htTrackerMedication == null) ? false : true;
		btnAddReminders.setEnabled(isEnabledForReminders);
		btnAddReminders.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle data = new Bundle();
				if (htTrackerMedication != null) {
					data.putSerializable("reminder_fk_id", htTrackerMedication.server_id);
					data.putSerializable("reminderName", htTrackerMedication.title);
				} else {
					data.putSerializable("reminder_fk_id", -1);
					data.putSerializable("reminderName","new med");
				}
				startFragment(HTMedicationReminderListFragment.class, data);
			}
		});

		TextView btnDoseTaken = (TextView)v.findViewById(R.id.btn_dose_taken);
		btnDoseTaken.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextUtils.showDoseTakenWorkflow(getActivity(), "activity", NoDoseClick, YesDoseClick);
			}
		});

		return v;
	}

	private HTMedicationImage getHTMedicationImage()
	{
		HTMedicationImageDelegate htMedicationImageDelegate = new HTMedicationImageDelegate();
		HTMedicationImage image = null;
		if (htTrackerMedication.server_id != null)
			image = htMedicationImageDelegate.getFirstByMedicationId(htTrackerMedication.server_id);

		if (image != null) return image;
		return htMedicationImageDelegate.getFirstByLocalFKmedicationId(htTrackerMedication.localId);
	}

	private boolean isDeviceSupportCamera() {
		if ((this.getActivity()).getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

//	private void getAllImages()	{
//		HTMedicationImageDelegate del = new HTMedicationImageDelegate();
//		List<HTMedicationImage> images = del.getAll();
//		for (HTMedicationImage i: images) {
//			String x = i.local_fk_medication_id == null ? "null" :  i.local_fk_medication_id.toString();
//		}
//	}

	private void captureImage() {
		final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this.getActivity());
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {

				if (items[item].equals("Take Photo")) {
//					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					dispatchTakePictureIntent();
//					//startActivityForResult(intent, REQUEST_TAKE_PHOTO);
				} else if (items[item].equals("Choose from Library")) {
					Intent intent = new Intent(	Intent.ACTION_PICK, 	MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, REQUEST_SELECT_FILE);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if the result is capturing Image
		if (requestCode == REQUEST_TAKE_PHOTO) {
			if (resultCode == RESULT_OK) {
				previewCapturedImage(data);

			} else if (resultCode == RESULT_CANCEL) {
				// user cancelled Image capture
				Toast.makeText(this.getActivity(),
						"User cancelled image capture", Toast.LENGTH_SHORT)
						.show();
			} else {
				// failed to capture image
				Toast.makeText(this.getActivity(),
						"Sorry! Failed to capture image", Toast.LENGTH_SHORT)
						.show();
			}
		} else if (requestCode == REQUEST_SELECT_FILE) {
			if (resultCode == RESULT_OK) {
				loadSelectedImage(data);
			} else if (resultCode == RESULT_CANCEL) {
				// user cancelled Image capture
				Toast.makeText(this.getActivity(),
						"User cancelled image capture", Toast.LENGTH_SHORT)
						.show();
			} else {
				// failed to capture image
				Toast.makeText(this.getActivity(),
						"Sorry! Failed to capture image", Toast.LENGTH_SHORT)
						.show();
			}
		}

	}

	private void loadSelectedImage(Intent data) {
		try {
			if (data.getData() != null) {
				Uri imageUri = data.getData();
				Bitmap photo = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), imageUri);
				setMedImage(photo);
			}
		}	catch(IOException e) {

		}
	}

	private void previewCapturedImage(Intent data) {
		try {
			//if (data.getExtras().get("data") != null) {

				setPic();
				//Bitmap photo = (Bitmap) data.getExtras().get("data");
				//setMedImage(photo);
			//}

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private void setMedImage(Bitmap bmp)
	{
		base64Image = bitmapToBase64(bmp);
		medimage.setVisibility(View.VISIBLE);
		medimage.setImageBitmap(bmp);
		medimage.setMinimumWidth(400);
		medimage.setMinimumHeight(400);
	}

	private String bitmapToBase64(Bitmap bitmap) {

		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
			byte[] byteArray = byteArrayOutputStream.toByteArray();
			return Base64.encodeToString(byteArray, Base64.DEFAULT);
		} catch(Exception e) {
			return null;
		}
	}

	private Bitmap base64ToBitmap(String b64) {
		try {
			byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
			return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
		} catch(Exception e) {
			return null;
		}
	}

	private void convertBmpToBase64String(Bitmap bmp)
	{

	}

	@Override
	public void reload()
	{
	}

	private void validateAndPost() {
		if (ViewUtils.validateFieldsEmpty(getView(), R.string.error_field_required, R.id.edit_name, R.id.edit_dosage)) {
			HTTrackerMedication medication = new HTTrackerMedication();
			medication.active = switchView.isChecked() ? 1 : 0;
			medication.dosagedescription = ViewUtils.getEditText(getView(), R.id.edit_dosage);
			medication.title = ViewUtils.getEditText(getView(), R.id.edit_name);
			medication.updated_at = new Date(System.currentTimeMillis());
			medication.synced = false;
			medication.deleted_at = null;

			if (htTrackerMedication == null) {
				medication.created_at = new Date(System.currentTimeMillis());
				medication.entity = medication.getClass().getSimpleName();

			} else {
				medication.localId = htTrackerMedication.localId;
				medication.server_id = htTrackerMedication.server_id;
				medication.created_at = htTrackerMedication.created_at;
				medication.entity = htTrackerMedication.entity;
			}

			if (medication.editable == null) medication.editable =1;

			HTTrackerMedicationDelegate htTrackerMedicationDelegate = new HTTrackerMedicationDelegate();
			htTrackerMedicationDelegate.add(medication);

			saveMedImage(medication);

			finish(RESULT_OK);

		}
	}

	private void saveMedImage(HTTrackerMedication medication) {

		//check if there is an image to save
		if (base64Image != null) {
			// check to see if image has changed
			if (base64Image != base64ImageOriginal) {
				HTMedicationImage medicationImage;
				boolean isNewMedImage = (htMedicationImage == null);
				if (isNewMedImage) {
					medicationImage = new HTMedicationImage();
					medicationImage.medication_id = medication.server_id;
					medicationImage.entity = "HTMedicationImage";
					medicationImage.deleted_at = null;
					medicationImage.local_fk_medication_id = medication.localId;
					medicationImage.created_at = new Date(System.currentTimeMillis());
				} else {
					medicationImage = htMedicationImage;
				}
				medicationImage.synced = (medicationImage.medication_id == null); // dont sync until we have a medication_id
				medicationImage.updated_at = new Date(System.currentTimeMillis());
				medicationImage.image = base64Image;
				HTMedicationImageDelegate htMedicationImageDelegate = new HTMedicationImageDelegate();
				htMedicationImageDelegate.add(medicationImage);
			}
		}
		//getAllImages();
	}

	private void zoomImageFromThumb2(final ImageView thumbView, final ImageView expandedImageView) {
		thumbView.setVisibility(View.INVISIBLE);
		expandedImageView.setImageBitmap(convertImageViewToBitmap(thumbView));
		//expandedImageView.setImageBitmap(base64ToBitmap(base64Image));
		expandedImageView.setVisibility(View.VISIBLE);
		expandedImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				thumbView.setVisibility(View.VISIBLE);
				expandedImageView.setVisibility(View.INVISIBLE);
			};
		});
	}

	private void zoomImageFromThumb(final ImageView thumbView, final ImageView expandedImageView, final ViewGroup container) {
		// If there's an animation in progress, cancel it
		// immediately and proceed with this one.
		if (mCurrentAnimator != null) {
			mCurrentAnimator.cancel();
		}

		// Load the high-resolution "zoomed-in" image.
		expandedImageView.setImageBitmap(convertImageViewToBitmap(thumbView));

		// Calculate the starting and ending bounds for the zoomed-in image.
		// This step involves lots of math. Yay, math.
		final Rect startBounds = new Rect();
		final Rect finalBounds = new Rect();
		final Point globalOffset = new Point();

		// The start bounds are the global visible rectangle of the thumbnail,
		// and the final bounds are the global visible rectangle of the container
		// view. Also set the container view's offset as the origin for the
		// bounds, since that's the origin for the positioning animation
		// properties (X, Y).
		thumbView.getGlobalVisibleRect(startBounds);

		container.getGlobalVisibleRect(finalBounds, globalOffset);
		startBounds.offset(-globalOffset.x, -globalOffset.y);
		finalBounds.offset(-globalOffset.x, -globalOffset.y);

		// Adjust the start bounds to be the same aspect ratio as the final
		// bounds using the "center crop" technique. This prevents undesirable
		// stretching during the animation. Also calculate the start scaling
		// factor (the end scaling factor is always 1.0).
		float startScale;
		if ((float) finalBounds.width() / finalBounds.height()
				> (float) startBounds.width() / startBounds.height()) {
			// Extend start bounds horizontally
			startScale = (float) startBounds.height() / finalBounds.height();
			float startWidth = startScale * finalBounds.width();
			float deltaWidth = (startWidth - startBounds.width()) / 2;
			startBounds.left -= deltaWidth;
			startBounds.right += deltaWidth;
		} else {
			// Extend start bounds vertically
			startScale = (float) startBounds.width() / finalBounds.width();
			float startHeight = startScale * finalBounds.height();
			float deltaHeight = (startHeight - startBounds.height()) / 2;
			startBounds.top -= deltaHeight;
			startBounds.bottom += deltaHeight;
		}

		// Hide the thumbnail and show the zoomed-in view. When the animation
		// begins, it will position the zoomed-in view in the place of the
		// thumbnail.
		thumbView.setAlpha(0f);
		expandedImageView.setVisibility(View.VISIBLE);

		// Set the pivot point for SCALE_X and SCALE_Y transformations
		// to the top-left corner of the zoomed-in view (the default
		// is the center of the view).
		expandedImageView.setPivotX(0f);
		expandedImageView.setPivotY(0f);

		// Construct and run the parallel animation of the four translation and
		// scale properties (X, Y, SCALE_X, and SCALE_Y).
		AnimatorSet set = new AnimatorSet();
		set
				.play(ObjectAnimator.ofFloat(expandedImageView, View.X,	startBounds.left, finalBounds.left))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.Y,	startBounds.top, finalBounds.top))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
				View.SCALE_Y, startScale, 1f));
		set.setDuration(mShortAnimationDuration);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mCurrentAnimator = null;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				mCurrentAnimator = null;
			}
		});
		set.start();
		mCurrentAnimator = set;

		// Upon clicking the zoomed-in image, it should zoom back down
		// to the original bounds and show the thumbnail instead of
		// the expanded image.
		final float startScaleFinal = startScale;
		expandedImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mCurrentAnimator != null) {
					mCurrentAnimator.cancel();
				}

				// Animate the four positioning/sizing properties in parallel,
				// back to their original values.
				AnimatorSet set = new AnimatorSet();
				set.play(ObjectAnimator
						.ofFloat(expandedImageView, View.X, startBounds.left))
						.with(ObjectAnimator
								.ofFloat(expandedImageView,
										View.Y,startBounds.top))
						.with(ObjectAnimator
								.ofFloat(expandedImageView,
										View.SCALE_X, startScaleFinal))
						.with(ObjectAnimator
								.ofFloat(expandedImageView,
										View.SCALE_Y, startScaleFinal));
				set.setDuration(mShortAnimationDuration);
				set.setInterpolator(new DecelerateInterpolator());
				set.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						thumbView.setAlpha(1f);
						expandedImageView.setVisibility(View.GONE);
						mCurrentAnimator = null;
					}

					@Override
					public void onAnimationCancel(Animator animation) {
						thumbView.setAlpha(1f);
						expandedImageView.setVisibility(View.GONE);
						mCurrentAnimator = null;
					}
				});
				set.start();
				mCurrentAnimator = set;
			}
		});
	}

	private Bitmap convertImageViewToBitmap(ImageView v){
		try {
			return ((BitmapDrawable) v.getDrawable()).getBitmap();
		} catch (Exception e) {
			return null;
		}
	}

	String mCurrentPhotoPath;

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = (this.getActivity()).getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity((this.getActivity()).getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File
				//...
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				Uri photoURI = FileProvider.getUriForFile(this.getContext(),	"com.example.android.fileprovider",	photoFile);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}

	private Bitmap setPic() {
		// Get the dimensions of the View
		int targetW = expandedImageView.getWidth();
		int targetH = expandedImageView.getHeight();

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();

		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int bitmapByteCount= BitmapCompat.getAllocationByteCount(bitmap);
		expandedImageView.setImageBitmap(bitmap);
		setMedImage(bitmap);
		return bitmap;
	}
}
