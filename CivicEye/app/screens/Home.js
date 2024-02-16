

import React, { useState } from "react";
import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View, SafeAreaView, Button, TextInput, Image, Alert , Modal, Pressable} from 'react-native';
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
    label: 'City Maintenance',
    image: {
      uri: 'https://www.vigcenter.com/public/all/images/default-image.jpg',
    },
  },

];

const garbageSubCatData = [
   {
    value: '1.1',
    label: 'Biodegradabale',
    image: {
      uri: 'https://www.vigcenter.com/public/all/images/default-image.jpg',
    },
  },

   {
    value: '1.2',
    label: 'Non-biodegradabale',
    image: {
      uri: 'https://www.vigcenter.com/public/all/images/default-image.jpg',
    },
  },

];
  
const citySubCatData = [
   {
    value: '2.1',
    label: 'Water leakage',
    image: {
      uri: 'https://www.vigcenter.com/public/all/images/default-image.jpg',
    },
  },

   {
    value: '2.2',
    label: 'Broken infrastructure',
    image: {
      uri: 'https://www.vigcenter.com/public/all/images/default-image.jpg',
    },
  },

  ];
  

function Home() {
  const [isDropdownVisible, setIsDropdownVisible] = useState(false);
   const [isClicked, setIsClicked] = useState(false);
  const [file, setFile] = useState(null);
  const [isMainCatSelected, setIsMainCatSelected] = useState(false);
  const [text, onChangeText] = React.useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [mainCatSelected, mainCatValue] = React.useState('');
  const [value, setValue] = useState(null);
  // const [number, onChangeNumber] = React.useState('');


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

    const openSubCategory = () => {
      // Handle description opening here
    setIsMainCatSelected(!isMainCatSelected);
      
  };

  const saveSelectedCat = () => {
    
  }

  return (
    <SafeAreaView style={styles.container}>

       <Modal
        animationType="slide"
        transparent={true}
        visible={modalVisible}
        onRequestClose={() => {
          Alert.alert('Modal has been closed.');
          setModalVisible(!modalVisible);
        }}>
        <View style={styles.centeredView}>
          <View style={styles.modalView}>
            



      {modalVisible && mainCatSelected === "Garbage" ? (
      <View style={styles.subCatContainer}>
        {/* <Text style={styles.selectCategoryText}>Select category</Text> */}
        <SelectCountry
          style={styles.dropdown}
          selectedTextStyle={styles.selectedTextStyle}
          placeholderStyle={styles.placeholderStyle}
          imageStyle={styles.imageStyle}
          iconStyle={styles.iconStyle}
          maxHeight={200}
          value={'1'}
          data={garbageSubCatData}
          valueField="value"
          labelField="label"
          imageField="image"
          placeholder="Select category"
          searchPlaceholder="Search..."
          onChange={item => {
            setValue(item.value);
            console.log(item.label);
            // openDescription();
            // setModalVisible(!modalVisible);
            
          }}
   
        />
      </View>

      )
              : null}
            


             {modalVisible && mainCatSelected === "City Maintenance" ? (
      <View style={styles.subCatContainer}>
        {/* <Text style={styles.selectCategoryText}>Select category</Text> */}
        <SelectCountry
          style={styles.dropdown}
          selectedTextStyle={styles.selectedTextStyle}
          placeholderStyle={styles.placeholderStyle}
          imageStyle={styles.imageStyle}
          iconStyle={styles.iconStyle}
          maxHeight={200}
          value={'2'}
          data={citySubCatData}
          valueField="value"
          labelField="label"
          imageField="image"
          placeholder="Select category"
          searchPlaceholder="Search..."
          onChange={item => {
            setValue(item.value);
            console.log(item.label);
            // openDescription();
            // setModalVisible(!modalVisible);
            
          }}
   
        />
      </View>

      )
              : null}
            
            






            <Pressable
              style={[styles.button, styles.buttonClose]}
              onPress={() => setModalVisible(!modalVisible)}>
                <Text>Close</Text>
            </Pressable>
          </View>
        </View>
      </Modal>


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
          onChange={item => {
            setValue(item.value);
            console.log(item.label);
            // openDescription();
            setModalVisible(!modalVisible);
            saveSelectedCat(item.label);
            mainCatValue(item.label);
            // console.log(mainCatSelected, "f");
            
            // console.log("Main category value:", mainCatValue)
          }}
        //  onChange={(value) => (value)}
        onOpen={() => setIsDropdownVisible(true)}
        onClose={() => setIsDropdownVisible(false)}
        />
      </View>
      

      <Button
        style={{ width: "100%" }}
        color="#436850"
        title="Description"
        onPress={openDescription}
        />
      <Button
        style={{ width: "100%" }}
        color="#436850"
        title="Upload image"
        onPress={pickImage}
        />
      {isClicked ? (
        <View style={styles.description_popup}>
          <TextInput
            placeholder="Write the description here."
            onChangeText={newText=>onChangeText(newText)}
            style={styles.description_input}
            defaultValue={text}
            />
          

 <Text style={{padding: 100, fontSize: 50}}>
            {text
              // console.log(text);
            }
            
      </Text>
        </View>
      ) : null}



      


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
    backgroundColor: '#ADBC9F',
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
    backgroundColor: '#436850',
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
    backgroundColor: '#436850',
    borderRadius: 90,
    paddingHorizontal: 8,
  },
  // imageContainer: {

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



  centeredView: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 22,
  },
  modalView: {
    margin: 20,
    backgroundColor: 'white',
    borderRadius: 20,
    padding: 35,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 4,
    elevation: 5,
  },
  button: {
    borderRadius: 20,
    padding: 10,
    elevation: 2,
  },

  subCatContainer: {
     flexDirection: 'row',
    // alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#436850',
    padding: 10,
    // margin: 10,
    borderRadius: 5,
    // color:"orange"
  },
});

export default Home;
