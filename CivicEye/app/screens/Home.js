

import React, { useState } from "react";
import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View, SafeAreaView, Button, TextInput, Image, Alert } from 'react-native';
import * as ImagePicker from "expo-image-picker";
import { SelectCountry } from 'react-native-element-dropdown';

const local_data = [
    {
    value: '0',  // Change value to '0' for the placeholder
    label: 'Select category',
  },
  {
    value: '1',
    label: 'Garbage',
    image: {
      uri: 'https://www.vigcenter.com/public/all/images/default-image.jpg',
    },
  },
  {
    value: '2',
    label: 'Infrastructure',
    image: {
      uri: 'https://www.vigcenter.com/public/all/images/default-image.jpg',
    },
  },
  {
    value: '3',
    label: 'Animals',
    image: {
      uri: 'https://www.vigcenter.com/public/all/images/default-image.jpg',
    },
  },
  {
    value: '4',
    label: 'Something',
    image: {
      uri: 'https://www.vigcenter.com/public/all/images/default-image.jpg',
    },
  },
];

function Home() {
  const [isDropdownVisible, setIsDropdownVisible] = useState(false);
   const [isClicked, setIsClicked] = useState(false);
  const [file, setFile] = useState(null);

  // Stores any error message
  const [error, setError] = useState(null);

  // Function to pick an image from
  // the device's media library
  const pickImage = async () => {
    const { status } = await ImagePicker.requestMediaLibraryPermissionsAsync();

    if (status !== "granted") {
      // If permission is denied, show an alert
      Alert.alert(
        "Permission Denied",
        "Sorry, we need camera roll permission to upload images."
      );
    } else {
      // Launch the image library and get
      // the selected image
      const result = await ImagePicker.launchImageLibraryAsync();

      if (!result.canceled) {
        // If an image is selected (not cancelled),
        // update the file state variable
        setFile(result.uri);

        // Clear any previous errors
        setError(null);
      }
    }
  };

  const openDescription = () => {
    // Handle description opening here
    setIsClicked(!isClicked);
  };

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.heading}>Submit a new concern</Text>

      <View style={styles.dropdownContainer}>
        {/* <Text style={styles.selectCategoryText}>Select category</Text> */}
        <SelectCountry
          style={styles.dropdown}
          selectedTextStyle={styles.selectedTextStyle}
          placeholderStyle={styles.placeholderStyle}
          imageStyle={styles.imageStyle}
          iconStyle={styles.iconStyle}
          maxHeight={200}
          value={'1'}
          data={local_data}
          valueField="value"
          labelField="label"
          imageField="image"
          placeholder="Select category"
          searchPlaceholder="Search..."
          onChange={() => {}}
          onOpen={() => setIsDropdownVisible(true)}
          onClose={() => setIsDropdownVisible(false)}
        />
      </View>

      <Button
        style={{ width: "100%" }}
        color="orange"
        title="Upload image"
        onPress={pickImage}
      />
      <Button
        style={{ width: "100%" }}
        color="orange"
        title="Description"
        onPress={openDescription}
      />
      {isClicked ? (
        <View style={styles.description_popup}>
          <TextInput
            placeholder="Write the description here."
            style={styles.description_input}
          />
        </View>
      ) : null}
      {isDropdownVisible && (
        <View style={styles.dropdownOverlay} onTouchEnd={() => setIsDropdownVisible(false)} />
      )}

      {file ? (
        // Display the selected image
        <View style={styles.imageContainer}>
          <Image source={{ uri: file }} style={styles.image} />
        </View>
      ) : (
        // Display an error message if there's
        // an error or no image selected
        <Text style={styles.errorText}>{error}</Text>
      )}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'pink',
    paddingTop: 70,
  },
  heading: {
    fontWeight: "bold",
    fontSize: 26,
    textAlign: 'center',
  },
  dropdownContainer: {
    flexDirection: 'row',
    // alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: 'orange',
    padding: 10,
    // margin: 10,
    borderRadius: 5,
    // color:"orange"
  },
  selectCategoryText: {
    // color: "#ffff",
    textAlign: "center",
     fontWeight: "bold",
  },
  dropdown: {
    flex: 1,
    // height: 50,
    backgroundColor: 'orange',
    borderRadius: 90,
    paddingHorizontal: 8,
  },
  // imageContainer: {
  //   height: 100,
  //   width: 100,
  // },
  // image: {
  //   width: 200,
  //   height: 200,
  //   borderRadius: 8,
  // },
  dropdownOverlay: {
    flex: 1,
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
  },
  imageStyle: {
    width: 24,
    height: 24,
    borderRadius: 12,
  },
 placeholderStyle: {
    fontSize: 100,
    color: "#fff",
  },
  selectedTextStyle: {
    fontSize: 14,
    color: "#000",  // Set selected text color to black
  },
  
  // iconStyle: {
  //   width: 20,
  //   height: 20,
  // },

    description_popup: {
    width: "50%",
    backgroundColor: "#fff",
    height: 30,
    alignSelf:"center",
  },
  description_input: {
    height: 20,
    borderColor: "#8e8e8e",
    alignSelf: "center",
  },
});

export default Home;
