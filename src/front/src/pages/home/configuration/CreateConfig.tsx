import { useEffect, useState } from 'react';
import { Box, Button, Container, Grid } from '@mui/material';
import TitleComponent from "./components/TitleComponent";
import FirmwareComponent from "./components/FirmwareComponent";
import DescriptionComponent from "./components/DescriptionComponent";
import {
    CreateConfigurationData,
    ErrorState,
    getConfiguration,
    getTranscriptors,
    KeyValueConfiguration,
    postNewConfiguration,
    postUpdateConfiguration,
    Transcriptor
} from "../../../conf/configurationController";
import LoadingPage from '../../../sharedComponents/LoadingPage';
import SelectItemsList, { KeyValueItem } from '../../../sharedComponents/SelectItemsList';

function CreateConfig(props: {id?: number}) {
    const [errorState, setErrorState] = useState<ErrorState>({
        name: "",
        description: "",
        firmware: ""
    })
    const [title, setTitle] = useState("");
    const [firmware, setFirmware] = useState("");
    const [description, setDescription] = useState("");
    const [keys, setKeys] = useState<KeyValueItem<Transcriptor>[]>([]);
    const [selectedKeys, setSelectedKeys] = useState<KeyValueItem<Transcriptor>[]>([]);
    const [loaded, setLoaded] = useState(false);

    function check(): boolean {
        setErrorState({
            name: "",
            description: "",
            firmware: ""
        })

        let error = false;
        if (title.length === 0) {
            setErrorState((prevState) => ({
                ...prevState,
                name: "Le titre est obligatoire.",
            }));
            error = true;
        }

        if (title.length > 50) {
            setErrorState((prevState) => ({
                ...prevState,
                name: "Le titre ne peut pas dépasser 50 caractères.",
            }));
            error = true;
        }

        if (firmware.length === 0) {
            setErrorState((prevState) => ({
                ...prevState,
                firmware: "Le firmware est obligatoire.",
            }));
            error = true;
        }

        return error;
    }

    function handleSubmit() {
        if (!check()) {
            let resultData: CreateConfigurationData = {
                name: title,
                configuration: selectedKeys.map(keyValueTranscriptor => {
                    return {
                        key: keyValueTranscriptor.item,
                        value: keyValueTranscriptor.value,
                    }
                }),
                description: description,
                firmware: firmware,
            }
            // If props.id not undefined then it's an update
            if(props.id){
                postUpdateConfiguration(props.id, resultData)
                return
            }
            postNewConfiguration(resultData) // manage response to display error or success
        }
    }



    // Fetch the configuration
    useEffect(() => {
        getTranscriptors().then(transcriptors => {
            if (!transcriptors) {
                return
            }
            // Load transcriptors
            let items: KeyValueItem<Transcriptor>[] = transcriptors.map(transcriptor => {
                return {
                    id: transcriptor.id + "",
                    checker: item => {
                        return true;//RegExp(transcriptor.regex).exec(item)
                    },
                    item: transcriptor,
                    label: transcriptor.fullName,
                    value: ""
                }}
            )
            setKeys(items)

            if(!props.id){
                setLoaded(true)
                return
            }
            // If props.id is defined then it's an update
            getConfiguration(props.id).then(result => {
                if(!result){
                    console.log("Erreur lors de la récupération de la configuration.")
                    //setError("Erreur lors de la récupération de la configuration.")
                    return
                }
                let config: KeyValueConfiguration[] = Object.entries(JSON.parse(result.configuration)).map(([key, value]) => ({
                    key: transcriptors.find(t => t.id === Number(key)),
                    value: value,
                } as KeyValueConfiguration));

                var configurationKeys: number[] = config.map(conf => conf.key.id)
                setSelectedKeys(items.filter(transcriptor => configurationKeys.includes(transcriptor.item.id)))
                setTitle(result.name)
                setFirmware(result.firmware.version)
                setDescription(result.description)
                setLoaded(true)
            });
        })
    }, [props.id])

    return (
        <Box>
            {loaded && (
                <Container maxWidth="xl" sx={{mt: 4, mb: 4}}>
                <Grid container spacing={15}>
                    <Grid item xs={12} md={6}>
                        <Box>
                            <TitleComponent errorState={errorState} value={title} setValue={setTitle}/>
                            <FirmwareComponent errorState={errorState} value={firmware} setValue={setFirmware}/>
                            <DescriptionComponent errorState={errorState} value={description} setValue={setDescription}/>
                        </Box>
                    </Grid>
                    <Grid item xs={12} md={6}>
                        {/*<RightSection globalState={globalState} setGlobalState={setGlobalState} selectedKeys={selectedKeys} setSelectedKeys={setSelectedKeys} />*/}
                        <SelectItemsList
                            title='Champs de la configuration'
                            keyTitle='Clés'
                            items={keys}
                            selectKind='input'
                            selectedItems={selectedKeys}
                            setSelectedItems={setSelectedKeys}
                        />
                    </Grid>
                </Grid>
                <Box sx={{display: 'flex', justifyContent: 'center', mt: 4}}>
                    {/* TODO : fixer le bouton en bas */}
                    <Button sx={{borderRadius: 28}} onClick={handleSubmit} variant="contained"
                            color="primary">Valider</Button>
                </Box>
            </Container>
            )}
            {!loaded && (
                <LoadingPage/>
            )}
        </Box>

    );
}

export default CreateConfig;
