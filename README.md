# Mobile-Recipe-Generator
An Android application to classify ingredients, then fetch recipes using the classification.

If the original classification is incorrect, the user has the option to select a class out of the Top-5 highest probability predictions.

![GIF Demo](Demo.gif)

## Project Structure
With there being multiple compontents of the app (the CNN being trained, the server used to host the CNN for classification, the Android app itself), I've tried to separate each into its own sub-directory:

**Python Code** - Contains the sub-directories *Flask Server* & *PyTorch CNN Training*, with the component's corresponding scripts.

**MobileRecipeGenerator** - Contains the Android Studio project, seen within the demo provided above.

## Training the CNN
When training the neural network, I used the *Fruits 360* dataset. 

With the size of the dataset being so large, I decided to exclude it from the repository, but can be found [here](https://www.kaggle.com/moltean/fruits).

I also removed a large number of classes from the dataset (62 classes remain) as it contained objects not commonly seen in UK supermarkets - the class list can be seen under *Python Code > FlaskServer > app.py > class_types*.

## Flask Server
The CNN is hosted on a server, where the android app calls an endpoint.

The main reasoning behind this was to reduce the overall size of the app, whilst increasing classification speed (by it being ran on a pc with better specs).

### Running the Server
To run the server locally, simply start the *app.py* script.

## Running the Android App
The android app has been tested on multiple emulators within Android Studio (multiple Android versions), as well as on a real device (Samsung Galaxy S9 Edge) - as seen above.

To connect to the server, the constant ** within *Helper.java* needs to be changed to the ipv4 address of the device **running** the Flask server (typing *ipconfig* in a cmd session).

## Dependencies
Anaconda was used to install dependencies needed, the packages used are:

Python 3.9

- torch
- torchvision
- Flask
- numpy
