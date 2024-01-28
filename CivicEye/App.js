import React from "react";
import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View, Button, SafeAreaView, TouchableOpacity, useState } from 'react-native';
import { SelectCountry } from 'react-native-element-dropdown';
// import React, { useState } from 'react';
//   import { StyleSheet } from 'react-native';

// import {launchCamera, launchImageLibrary} from 'react-native-image-picker';
import Home from "./app/screens/Home";

export default function App() {
  return (
    <Home />

  );
}



// export default function App() {
//   const [selectImage, setselectImage] = useState('')
//   const ImagePicker = () => {
//     let options = {
//       storageOptions: {
//         path: "image"
//       },
//     };

//     launchImageLibrary(options, response => {
//       setselectImage(response.assets[0].uri);
//       console.log(response.assets[0].uri);
//     });
//   };
//   return (
//     <SafeAreaView style={{ flex: 1 }}>
//       <View style={{ height: 400, width: "100%" }}>
//         <Image style={{ height: 400, width: "100%" }}
//           source={{uri:selectImage}}
//         />
//         <TouchableOpacity
//           onPress={() => {
//             ImagePicker();
//           }}
//           style={{
//             marginTop: 20,
//             height: 50,
//             width: "60%",
//             borderRadius: 20,
//             backgroundColor: 'skyblue',
//           justifyContent:"center",
//           alignItems:"center",
//           slignSelf:"center",
//           }}>
//           <Text style={{fontSize:20}}>Gallery</Text>
//         </TouchableOpacity>
//       </View>
//        </SafeAreaView>
//   );
// }


