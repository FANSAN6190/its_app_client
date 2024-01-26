import React, { useState } from "react";
import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View, SafeAreaView, Button, TextInput } from 'react-native';
import * as ImagePicker from "expo-image-picker";



function Home() {
  const [isClicked, setIsClicked] = useState("false");
   const [file, setFile] = useState(null);
  
    // Stores any error message
    const [error, setError] = useState(null);
  
    // Function to pick an image from
    //the device's media library
    const pickImage = async () => {
        const { status } = await ImagePicker.
            requestMediaLibraryPermissionsAsync();
  
        if (status !== "granted") {
  
            // If permission is denied, show an alert
            Alert.alert(
                "Permission Denied",
                `Sorry, we need camera
                 roll permission to upload images.`
            );
        } else {
  
            // Launch the image library and get
            // the selected image
            const result =
                await ImagePicker.launchImageLibraryAsync();
  
            if (!result.cancelled) {
  
                // If an image is selected (not cancelled),
                // update the file state variable
                setFile(result.uri);
  
                // Clear any previous errors
                setError(null);
            }
        }
    };
  
  const open_description = () => {
    setIsClicked(!isClicked);
   

};
  
  
  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.heading}>Submit a new concern</Text>
      <Button
        style={{ width: "100%" }}
        color="orange"
        title="Select category"
      />
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
        onPress={()=>open_description()}
      />
      
      {isClicked ? <View style={styles.description_popup}>
        <TextInput
        placeholder="Write the description here."
        style={styles.description_input}
    
      />
      </View>:null}
        

      {file ? (
                // Display the selected image
                <View style={styles.imageContainer}>
                    <Image source={{ uri: file }}
                        style={styles.image} />
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
    // justifyContent: 'center',
    // alignItems: 'center',
    paddingTop: 70,
  },

  heading: {
    fontWeight: "bold",
    fontSize: 20, // Adjust the font size as needed
    textAlign: 'center', // Center the text
  },
  description_popup: {
    width: "50%",
    backgroundColor: "#fff",
    height: 30,
    alignSelf:"center",
  },

  description_input: {
    height: 20,
    // borderRadius:10,
    // borderWidth: 0.5,
    borderColor: "#8e8e8e",
    alignSelf: "center",
    // padd
  },

   imageContainer: {
        
  },
    image: {
        width: 200,
        height: 200,
        borderRadius: 8,
    },
});

export default Home;

